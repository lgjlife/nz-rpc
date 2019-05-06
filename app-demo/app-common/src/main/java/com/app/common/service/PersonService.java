package com.app.common.service;

import java.util.concurrent.CompletableFuture;

public interface PersonService {

    CompletableFuture<String> queryPerson(String name);
    String doSomeThing();

}
