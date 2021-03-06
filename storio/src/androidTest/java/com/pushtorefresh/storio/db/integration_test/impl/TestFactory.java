package com.pushtorefresh.storio.db.integration_test.impl;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFactory {

    private static final AtomicInteger counter = new AtomicInteger(0);

    @NonNull public static User newUser() {
        return new User(null, "user" + counter.incrementAndGet() + "@example.com");
    }

    @NonNull public static List<User> newUsers(int quantity) {
        final List<User> users = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            users.add(newUser());
        }

        return users;
    }
}
