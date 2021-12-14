package com.example.ex2.rootService;

import ro.ubbcluj.map.Service.NetworkService;

public class RootService {

    private NetworkService networkService;

    public RootService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    private void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }
}
