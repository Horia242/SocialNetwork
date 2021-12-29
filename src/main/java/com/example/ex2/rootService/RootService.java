package com.example.ex2.rootService;

import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.Service.NetworkServiceForPaginatedDbRepo;

public class RootService {

    private NetworkService networkService;
    private NetworkServiceForPaginatedDbRepo networkServicePag;
    public RootService(NetworkService networkService, NetworkServiceForPaginatedDbRepo networkServicePag) {
        this.networkService = networkService;
        this.networkServicePag = networkServicePag;
    }

    public NetworkService getNetworkService() {
        return networkService;
    }

    private void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    public NetworkServiceForPaginatedDbRepo getNetworkServicePag() {
        return networkServicePag;
    }

    public void setNetworkServicePag(NetworkServiceForPaginatedDbRepo networkServicePag) {
        this.networkServicePag = networkServicePag;
    }
}
