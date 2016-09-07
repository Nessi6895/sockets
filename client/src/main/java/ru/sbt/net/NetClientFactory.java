package ru.sbt.net;

import java.lang.reflect.Proxy;
import java.util.List;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.reflect.Proxy.newProxyInstance;

public class NetClientFactory {
    private final String host;
    private final int port;

    public NetClientFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public <T> T createClient(Class<T> interfaceClass) {
        return (T) newProxyInstance(getSystemClassLoader(), new Class[]{interfaceClass}, new ClientInvocationHandler(host, port));
    }

    public static void main(String[] args) {
        NetClientFactory factory = new NetClientFactory("localhost", 5000);
        Calculator client = factory.createClient(Calculator.class);
        double calculate = client.calculate(1, 2);
        System.out.println(calculate);
    }
}
