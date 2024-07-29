package ecommerce;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Objects;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println(App.class.getCanonicalName());



        ProductInfoGrpc.ProductInfoImplBase g = new ProductInfoGrpc.ProductInfoImplBase() {
            @Override
            public void addProduct(ProductOuterClass.Product request, StreamObserver<ProductIDOuterClass.ProductID> responseObserver) {
                System.out.println("## addProduct " + request.getName());
                ProductIDOuterClass.ProductID productID = ProductIDOuterClass.ProductID
                        .newBuilder()
                        .setValue("100")
                        .build();
                responseObserver.onNext(productID);
                responseObserver.onCompleted();
            }

            @Override
            public void getProduct(ProductIDOuterClass.ProductID request, StreamObserver<ProductOuterClass.Product> responseObserver) {
                super.getProduct(request, responseObserver);
            }
        };

        Server server = ServerBuilder.forPort(8080)
                .addService(g)
                .build()
                .start();

        System.out.println("grpc " + 8080);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (Objects.nonNull(server)) {
                    server.shutdown();
                }
            }
        }));

        ManagedChannel managedChannel =
                ManagedChannelBuilder.forAddress("localhost", 8080)
                        .usePlaintext()
                        .build();

        ProductInfoGrpc.ProductInfoBlockingStub stub =
                ProductInfoGrpc.newBlockingStub(managedChannel);

        ProductOuterClass.Product product = ProductOuterClass.Product.newBuilder()
                .setName("Apple iPhone 11")
                .setDescription("")
                .setPrice(3600.0f)
                .build();
        ProductIDOuterClass.ProductID productID = stub.addProduct(product);
        System.out.println(productID.getValue());

        server.awaitTermination();



    }
}
