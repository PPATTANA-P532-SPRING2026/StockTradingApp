package com.pm.tradesimulator.service.market;
import java.math.BigDecimal;
import java.util.Map;
public interface PriceObserver {

        void onPriceUpdate(Map<String, BigDecimal> prices);


}
