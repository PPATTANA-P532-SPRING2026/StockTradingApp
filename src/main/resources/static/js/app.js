const REFRESH_INTERVAL = 5000;

//  on page load
window.onload = function () {
    refreshAll();
    setInterval(refreshAll, REFRESH_INTERVAL);
};

//  refresh everything
function refreshAll() {
    fetchPrices();
    fetchPortfolio();
    fetchTradeHistory();
}

//  fetch market prices
function fetchPrices() {
    fetch('/api/market')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('pricesTable');
            tbody.innerHTML = '';
            Object.entries(data).forEach(([ticker, price]) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><strong>${ticker}</strong></td>
                    <td>$${parseFloat(price).toFixed(2)}</td>
                `;
                tbody.appendChild(row);
            });
            document.getElementById('lastUpdated').textContent =
                'Last updated: ' + new Date().toLocaleTimeString();
        })
        .catch(err => console.error('Error fetching prices:', err));
}

//  fetch portfolio
function fetchPortfolio() {
    fetch('/api/portfolio')
        .then(res => res.json())
        .then(data => {
            // update cash and total value
            document.getElementById('cashBalance').textContent =
                parseFloat(data.cash).toFixed(2);
            document.getElementById('totalValue').textContent =
                parseFloat(data.totalValue).toFixed(2);

            // update holdings table
            const holdingsTbody = document.getElementById('holdingsTable');
            holdingsTbody.innerHTML = '';
            const holdings = data.holdings;
            if (Object.keys(holdings).length === 0) {
                holdingsTbody.innerHTML =
                    '<tr><td colspan="6">No holdings yet</td></tr>';
            } else {
                Object.entries(holdings).forEach(([ticker, h]) => {
                    const pnl = (h.currentValue - (h.averageCost * h.quantity)).toFixed(2);
                    const pnlClass = pnl >= 0 ? 'positive' : 'negative';
                    const pnlSign = pnl >= 0 ? '+' : '';
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td><strong>${ticker}</strong></td>
                        <td>${h.quantity}</td>
                        <td>$${parseFloat(h.averageCost).toFixed(2)}</td>
                        <td>$${parseFloat(h.currentPrice).toFixed(2)}</td>
                        <td>$${parseFloat(h.currentValue).toFixed(2)}</td>
                        <td class="${pnlClass}">${pnlSign}$${pnl}</td>
                    `;
                    holdingsTbody.appendChild(row);
                });
            }

            // update pending orders table
            const pendingTbody = document.getElementById('pendingOrdersTable');
            pendingTbody.innerHTML = '';
            const pendingOrders = data.pendingOrders;
            if (pendingOrders.length === 0) {
                pendingTbody.innerHTML =
                    '<tr><td colspan="7">No pending orders</td></tr>';
            } else {
                pendingOrders.forEach(order => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${order.id.substring(0, 8)}...</td>
                        <td><strong>${order.ticker}</strong></td>
                        <td>${order.side}</td>
                        <td>${order.quantity}</td>
                        <td>${order.limitPrice ? '$' + parseFloat(order.limitPrice).toFixed(2) : 'N/A'}</td>
                        <td>${new Date(order.timestamp).toLocaleTimeString()}</td>
                        <td>
                            <button class="cancel-btn"
                                onclick="cancelOrder('${order.id}')">
                                Cancel
                            </button>
                        </td>
                    `;
                    pendingTbody.appendChild(row);
                });
            }
        })
        .catch(err => console.error('Error fetching portfolio:', err));
}

//  fetch trade history
function fetchTradeHistory() {
    fetch('/api/trades')
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('tradeHistoryTable');
            tbody.innerHTML = '';
            if (data.length === 0) {
                tbody.innerHTML =
                    '<tr><td colspan="6">No trades yet</td></tr>';
            } else {
                // show most recent trades first
                [...data].reverse().forEach(trade => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${new Date(trade.timestamp).toLocaleTimeString()}</td>
                        <td><strong>${trade.ticker}</strong></td>
                        <td>${trade.side}</td>
                        <td>${trade.quantity}</td>
                        <td>$${parseFloat(trade.price).toFixed(2)}</td>
                        <td>$${parseFloat(trade.total).toFixed(2)}</td>
                    `;
                    tbody.appendChild(row);
                });
            }
        })
        .catch(err => console.error('Error fetching trades:', err));
}

// place order
function placeOrder() {
    const ticker = document.getElementById('ticker').value;
    const side = document.getElementById('side').value;
    const type = document.getElementById('orderType').value;
    const quantity = document.getElementById('quantity').value;
    const limitPrice = document.getElementById('limitPrice').value;

    const body = { ticker, side, type, quantity };
    if (type === 'LIMIT' && limitPrice) {
        body.limitPrice = limitPrice;
    }

    fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    })
        .then(res => {
            if (res.ok) {
                return res.json().then(data => {
                    showMessage('orderMessage',
                        `Order placed successfully!`, 'success');
                    refreshAll();
                });
            } else {
                return res.text().then(err => {
                    showMessage('orderMessage', err, 'error');
                });
            }
        })
        .catch(err => console.error('Error placing order:', err));
}

//  cancel order
function cancelOrder(orderId) {
    fetch(`/api/orders/${orderId}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                showMessage('orderMessage',
                    'Order cancelled successfully', 'success');
                refreshAll();
            } else {
                showMessage('orderMessage',
                    'Could not cancel order', 'error');
            }
        })
        .catch(err => console.error('Error cancelling order:', err));
}

//toggle limit price field
function toggleLimitPrice() {
    const type = document.getElementById('orderType').value;
    const limitPriceGroup = document.getElementById('limitPriceGroup');
    limitPriceGroup.style.display = type === 'LIMIT' ? 'block' : 'none';
}

// show message helper
function showMessage(elementId, message, type) {
    const el = document.getElementById(elementId);
    el.textContent = message;
    el.className = type;
    setTimeout(() => { el.textContent = ''; }, 4000);
}