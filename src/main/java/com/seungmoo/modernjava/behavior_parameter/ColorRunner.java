package com.seungmoo.modernjava.behavior_parameter;

public class ColorRunner {

    public void run() {
        TriFunction<Color> colorTriFunction = (red, green, blue) -> new Color(red, green, blue);
        TriFunction<Color> colorMethodRefer = Color::new;
    }

}
