package com.visma.iotajava.controller;


import com.google.gson.Gson;
import jota.IotaAPI;
import jota.dto.response.GetNewAddressResponse;
import jota.error.ArgumentException;
import jota.model.Transfer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/iota")
@MultipartConfig(fileSizeThreshold = 20971520)
public class IotaController {

    IotaAPI api = new IotaAPI.Builder()
            .protocol("https")
            .host("nodes.devnet.iota.org")
            .port("443")
            .build();

    /*@GetMapping("/getNodeInfo")
    ResponseEntity<String> getNodeInfo() throws InterruptedException, IOException {
        OkHttpClient client = new OkHttpClient();
        client = CommonUtils.trustAllSslClient(client);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json"), "{\"command\":\"getNodeInfo\"}");
        Request request = new Request.Builder().url("http://localhost:14265").method("POST", body).build();
        Request authenticatedRequest = request.newBuilder().header("X-IOTA-API-VERSION", "1.4.1").build();
        Response response = client.newCall(authenticatedRequest).execute();
        String responseStr = response.body().string();
        return ResponseEntity.ok(responseStr);
    }*/



    @GetMapping("/createWallet/{seed}")
    ResponseEntity<GetNewAddressResponse> createWallet(@PathVariable String seed) throws InterruptedException {

        try {
            GetNewAddressResponse response = api.generateNewAddresses(seed, 3 , true, 1);
            System.out.println("Address ---------->" + response.getAddresses());
            String address  = response.first();
            Transfer transfer = new Transfer(address, 0 , "", "");
            System.out.println("Transfer ---------->" + transfer);
            Thread.sleep((2000));
            api.sendTransfer(seed, 3, 10, 13, Collections.singletonList(transfer), null, null, false, true, null);
            System.out.println("Response ---------->" + response);
            return ResponseEntity.ok(response);
        } catch (ArgumentException e) {
            List<String> resp = new ArrayList<>();
            resp.add(e.getMessage());
            return ResponseEntity.ok(GetNewAddressResponse.create(resp,0l));
        }
    }


    @GetMapping("/getBalance/{address}")
    ResponseEntity<String> getBalance(@PathVariable String address) {
        List<String> resp = new ArrayList<>();
        resp.add(address);
        try {
            return ResponseEntity.ok(new Gson().toJson(api.getBalances(100, resp)));
        } catch (ArgumentException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
