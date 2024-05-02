package com.swiggy.Wallet;

import com.swiggy.Wallet.entity.Money;
import conversion.ConversionRequest;
import conversion.ConversionResponse;
import conversion.ConversionServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class Converter {

    public static Money convert(double amount, Currency baseCurrency, Currency targetCurrency){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
        ConversionServiceGrpc.ConversionServiceBlockingStub client = ConversionServiceGrpc.newBlockingStub(channel);
        try {
            ConversionResponse response = client.convertCurrency(
                    ConversionRequest.newBuilder().setTransferAmount(amount)
                            .setBaseCurrency(baseCurrency.toString())
                            .setSourceCurrency(targetCurrency.toString())
                            .build());

            double convertedAmount = response.getConvertedAmount();
            BigDecimal formattedValue = BigDecimal.valueOf(convertedAmount).setScale(2, RoundingMode.HALF_EVEN);

            return new Money(formattedValue, baseCurrency);
        } catch (StatusRuntimeException s) {
            throw new RuntimeException("Error while converting");
        }
    }
}
