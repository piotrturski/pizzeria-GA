package com.graphaware.pizzeria.service;

import com.graphaware.pizzeria.model.Pizza;
import lombok.experimental.UtilityClass;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

@UtilityClass
class PriceCalculator {

    public final String PINEAPPLE = "pineapple";

    Double computeAmount(@Nullable List<Pizza> pizzas) {
        boolean pineappleDiscount = emptyIfNull(pizzas).stream().anyMatch(p -> p.getToppings().contains(PINEAPPLE));

        return StreamEx.of(emptyIfNull(pizzas))
                .mapToEntry(p -> p.getToppings().contains(PINEAPPLE), Pizza::getPrice)
                .mapKeyValue((hasPineaple, price) -> price * (pineappleDiscount && !hasPineaple ? 0.9 : 1))
                .reverseSorted()
                .zipWith(IntStreamEx.ints())
                .removeValues(position -> position % 3 == 2)
                .keys()
                .reduce(0d, Double::sum);
    }

    private <E> List<E> emptyIfNull(@Nullable List<E> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
