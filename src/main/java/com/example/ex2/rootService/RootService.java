package com.example.ex2.rootService;

import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.Service.NetworkServiceForPaginatedDbRepo;

public class RootService {

    private NetworkServiceForPaginatedDbRepo networkServicePag;
    public RootService( NetworkServiceForPaginatedDbRepo networkServicePag) {
        this.networkServicePag = networkServicePag;
    }

    public NetworkServiceForPaginatedDbRepo getNetworkServicePag() {
        return networkServicePag;
    }

    public void setNetworkServicePag(NetworkServiceForPaginatedDbRepo networkServicePag) {
        this.networkServicePag = networkServicePag;
    }
}
