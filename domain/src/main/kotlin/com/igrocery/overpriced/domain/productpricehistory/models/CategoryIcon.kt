package com.igrocery.overpriced.domain.productpricehistory.models

import androidx.annotation.DrawableRes
import com.igrocery.overpriced.domain.R

enum class CategoryIcon(
    @DrawableRes val iconRes: Int,
) {
    // unspecified
    NoCategory(R.drawable.ic_question_svgrepo_com),
    Groceries(R.drawable.ic_groceries_svgrepo_com),

    // vegetables
    Broccoli(R.drawable.ic_broccoli_svgrepo_com),
    Carrot(R.drawable.ic_carrot_svgrepo_com),
    Corn(R.drawable.ic_corn_svgrepo_com),
    Eggplant(R.drawable.ic_eggplant_svgrepo_com),
    Peas(R.drawable.ic_peas_svgrepo_com),
    Onion(R.drawable.ic_onion_svgrepo_com),
    Potatoes(R.drawable.ic_potatoes_svgrepo_com),
    Pickles(R.drawable.ic_pickles_svgrepo_com),

    // fruits
    Apple(R.drawable.ic_apple_svgrepo_com),
    Orange(R.drawable.ic_orange_svgrepo_com),
    Banana(R.drawable.ic_banana_svgrepo_com),
    BlueBerries(R.drawable.ic_blue_berries_svgrepo_com),
    Watermelon(R.drawable.ic_watermelon_svgrepo_com),

    // meat
    Steak(R.drawable.ic_steak_svgrepo_com),
    Meat(R.drawable.ic_meat_svgrepo_com),
    Fish(R.drawable.ic_fish_svgrepo_com),
    Salmon(R.drawable.ic_salmon_svgrepo_com),
    Sushi(R.drawable.ic_sushi_svgrepo_com),

    // dairy
    Cheese(R.drawable.ic_cheese_svgrepo_com),
    Egg(R.drawable.ic_egg_svgrepo_com),
    Milk(R.drawable.ic_milk_svgrepo_com),

    // grains
    Baguette(R.drawable.ic_baguette_svgrepo_com),
    Toast(R.drawable.ic_toast_svgrepo_com),
    Cereals(R.drawable.ic_cereals_svgrepo_com),
    Hamburger(R.drawable.ic_hamburger_svgrepo_com),
    Sandwich(R.drawable.ic_sandwich_svgrepo_com),

    // beverages
    CoffeeBeans(R.drawable.ic_coffee_beans_svgrepo_com),
    Coke(R.drawable.ic_coke_svgrepo_com),
    Beer(R.drawable.ic_beer_svgrepo_com),

    // seasonings
    Chili(R.drawable.ic_chili_svgrepo_com),
    Spices(R.drawable.ic_spices_svgrepo_com),
    Honey(R.drawable.ic_honey_svgrepo_com),

    // snacks
    Candy(R.drawable.ic_candy_svgrepo_com),
    Chocolate(R.drawable.ic_chocolate_svgrepo_com),
    IceCream(R.drawable.ic_ice_cream_svgrepo_com),
    Fries(R.drawable.ic_fries_svgrepo_com),

    // tools & utensils
    CrockScrew(R.drawable.ic_corckscrew_svgrepo_com),
    CoffeeMaker(R.drawable.ic_coffee_maker_svgrepo_com),
    Cutlery(R.drawable.ic_cutlery_svgrepo_com),
    Grater(R.drawable.ic_grater_svgrepo_com),
    Pot(R.drawable.ic_pot_svgrepo_com),
    Strainer(R.drawable.ic_strainer_svgrepo_com),

}
