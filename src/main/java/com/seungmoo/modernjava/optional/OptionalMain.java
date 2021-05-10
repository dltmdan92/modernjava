package com.seungmoo.modernjava.optional;

import java.util.Optional;

public class OptionalMain {

    public void run() {
        String not_null_text = Optional.ofNullable("Not Null Text").get();
        Optional<Object> o = (Optional<Object>) Optional.of(null).get();
    }

}
