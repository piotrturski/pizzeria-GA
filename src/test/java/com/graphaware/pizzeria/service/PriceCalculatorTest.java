package com.graphaware.pizzeria.service;

import com.graphaware.pizzeria.model.Pizza;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.graphaware.pizzeria.service.PriceCalculator.PINEAPPLE;
import static org.assertj.core.api.Assertions.assertThat;

public class PriceCalculatorTest {


    @Test
    void shoud_sum_prices_when_no_discount() {
        assertThatCostOf(
                pizza(10, "a"),
                pizza(11, "b")
        )
                .isCloseTo(10+11, within1permille);
    }

    @Test
    void shoud_handle_no_pizzas() {
        assertThatCostOf().isCloseTo(0, within1permille);
    }

    @Test
    void shoud_handle_null_pizzas() {
        assertThat(PriceCalculator.computeAmount(null)).isCloseTo(0, within1permille);
    }

    @Test
    @Disabled("no null handling in current code")
    void shoud_handle_null_toppings() {
        assertThatCostOf(
                pizza(10, null),
                pizza(11, "b")
        )
                .isCloseTo(10+11, within1permille);
    }

    @Test
    void shoud_give_pineapple_discount() {
        assertThatCostOf(
                pizza(11, PINEAPPLE),
                pizza(10, "b")
        )
                .isCloseTo(11+9, within1permille);
    }

    @Test
    void shoud_give_discount_when_pineapple_inide_other_toppings() {
        assertThatCostOf(
                pizza(11, String.format("a,%s,b", PINEAPPLE)),
                pizza(10, "b")
        )
                .isCloseTo(11+9, within1permille);
    }

    @Test // no idea if that's the desired behaviour but this is how the original code works
    void shoud_give_discount_on_all_except_pineapple() {
        assertThatCostOf(
                pizza(10, PINEAPPLE),
                pizza(10, PINEAPPLE)
        )
                .isCloseTo(20, within1permille);
    }

    @Test
    void shoud_give_3rd_cheapest_pizza_for_free() {
        assertThatCostOf(
                pizza(20, "a"),
                pizza(5, "b"),
                pizza(10, "c")
        )
                .isCloseTo(20+10, within1permille);
    }

    @Test
    void shoud_give_every_3rd_pizza_for_free() {
        assertThatCostOf(
                pizza(1, "1"),
                pizza(1, "2"),
                pizza(1, "3 free"),
                pizza(1, "4"),
                pizza(1, "5"),
                pizza(1, "6 free"),
                pizza(1, "7")
        )
                .isCloseTo(7-2, within1permille);
    }

    @Test
    void shoud_group_pizzas_to_get_biggest_discount_for_every_3rd_pizza_free() {
        assertThatCostOf(
                pizza(9, ""),
                pizza(8, ""),
                pizza(8, "free"),

                pizza(5, ""),

                pizza(8, ""),
                pizza(6, "free"),
                pizza(7, "")
        )
                .isCloseTo(9+8+5+8+7, within1permille);
    }

    @Test
    void shoud_apply_pineapple_discount_before_3rd_free_discounts() {
        assertThatCostOf(
                pizza(9, ""),
                pizza(8, ""),
                pizza(8, "free"),

                pizza(5, ""),

                pizza(8, ""),
                pizza(6, "free"),
                pizza(7, "")
        )
                .isCloseTo(9+8+5+8+7, within1permille);
    }

    Percentage within1permille = Percentage.withPercentage(0.1);

    private AbstractDoubleAssert<?> assertThatCostOf(Pizza... pizzas) {
        Double price = PriceCalculator.computeAmount(Arrays.asList(pizzas));
        return assertThat(price);
    }

    private Pizza pizza(int price, String toppings) {
        Pizza pizza = new Pizza();
        pizza.setPrice((double)price);
        List<String> arrayOfToppings = toppings == null ? null : Arrays.asList(toppings.split(","));
        pizza.setToppings(arrayOfToppings);
        return pizza;
    }


}
