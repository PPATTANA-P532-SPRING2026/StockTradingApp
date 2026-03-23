const API_BASE = window.location.hostname === 'localhost'
    ? ''
    : 'https://stocktradingapp.onrender.com';

const REFRESH_INTERVAL = 5000;

window.onload = function () {
    refreshAll();
    setInterval(refreshAll, REFRESH_INTERVAL);
};

function refreshAll() {
    fetchPrices();
    fetchPortfolio();
    fetchTradeHistory();
    fetchStrategy();
    fetchBadgeCount();
    fetchActiveUser();
}

function fetchPrices() {
    fetch(`${API_BASE}/api/market`)
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

function fetchPortfolio() {
    fetch(`${API_BASE}/api/portfolio`)
        .then(res => res.json())
        .then(data => {
            document.getElementById('cashBalance').textContent =
                parseFloat(data.cash).toFixed(2);
            document.getElementById('totalValue').textContent =
                parseFloat(data.totalValue).toFixed(2);

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

function fetchTradeHistory() {
    fetch(`${API_BASE}/api/trades`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('tradeHistoryTable');
            tbody.innerHTML = '';
            if (data.length === 0) {
                tbody.innerHTML =
                    '<tr><td colspan="6">No trades yet</td></tr>';
            } else {
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

    fetch(`${API_BASE}/api/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    })
        .then(res => {
            if (res.ok) {
                return res.json().then(data => {
                    showMessage('orderMessage', 'Order placed successfully!', 'success');
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

function cancelOrder(orderId) {
    fetch(`${API_BASE}/api/orders/${orderId}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) {
                showMessage('orderMessage', 'Order cancelled successfully', 'success');
                refreshAll();
            } else {
                showMessage('orderMessage', 'Could not cancel order', 'error');
            }
        })
        .catch(err => console.error('Error cancelling order:', err));
}

function toggleLimitPrice() {
    const type = document.getElementById('orderType').value;
    const limitPriceGroup = document.getElementById('limitPriceGroup');
    limitPriceGroup.style.display = type === 'LIMIT' ? 'block' : 'none';
}

function showMessage(elementId, message, type) {
    const el = document.getElementById(elementId);
    el.textContent = message;
    el.className = type;
    setTimeout(() => { el.textContent = ''; }, 4000);
}

function fetchStrategy() {
    fetch(`${API_BASE}/api/strategy`)
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('strategySelect');
            const name = data.strategy;
            if (name.includes('MeanReversion'))       select.value = 'meanReversion';
            else if (name.includes('TrendFollowing')) select.value = 'trendFollowing';
            else                                       select.value = 'randomWalk';
        })
        .catch(err => console.error('Error fetching strategy:', err));
}

function changeStrategy() {
    const strategy = document.getElementById('strategySelect').value;
    fetch(`${API_BASE}/api/strategy`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ strategy })
    })
    .then(res => res.json())
    .then(data => {
        showMessage('strategyMessage',
            `Strategy changed to ${strategy}`, 'success');
    })
    .catch(err => console.error('Error changing strategy:', err));
}

// ── fetch badge count ─────────────────────────────────────────────────
function fetchBadgeCount() {
    fetch(`${API_BASE}/api/notifications/badge`)
        .then(res => res.json())
        .then(data => {
            const badge = document.getElementById('badgeBubble');
            if (data.badgeCount > 0) {
                badge.textContent = data.badgeCount;
                badge.style.display = 'inline';
            } else {
                badge.style.display = 'none';
            }
        })
        .catch(err => console.error('Error fetching badge:', err));
}

// ── update notification channels ─────────────────────────────────────
function updateChannels() {
    const channels = ['console']; // console always included
    if (document.getElementById('chEmail').checked)     channels.push('email');
    if (document.getElementById('chSms').checked)       channels.push('sms');
    if (document.getElementById('chDashboard').checked) channels.push('dashboard');

    fetch(`${API_BASE}/api/notifications/channels`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ channels })
    })
    .then(res => res.json())
    .then(data => {
        showMessage('notificationMessage',
            'Notification channels updated', 'success');
    })
    .catch(err => console.error('Error updating channels:', err));
}

// ── reset badge count ─────────────────────────────────────────────────
function resetBadge() {
    fetch(`${API_BASE}/api/notifications/reset`, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            document.getElementById('badgeBubble').style.display = 'none';
            showMessage('notificationMessage', 'Badge reset', 'success');
        })
        .catch(err => console.error('Error resetting badge:', err));

}

// fetch active user on load
function fetchActiveUser() {
    fetch(`${API_BASE}/api/users/active`)
        .then(res => res.json())
        .then(data => {
            const select = document.getElementById('userSelect');
            select.value = data.id;
        })
        .catch(err => console.error('Error fetching active user:', err));
}

//  switch active user
function switchUser() {
    const userId = document.getElementById('userSelect').value;
    fetch(`${API_BASE}/api/users/active`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId })
    })
    .then(res => res.json())
    .then(data => {
        showMessage('userMessage',
            `Switched to ${data.name}`, 'success');
        refreshAll();   // refresh portfolio for new user
    })
    .catch(err => console.error('Error switching user:', err));
}