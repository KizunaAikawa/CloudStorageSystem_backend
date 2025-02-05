package ryu.cloudstoragesystem_backend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ryu.cloudstoragesystem_backend.auth.service.PublicKeyService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class PublicKeyController {
    @Value("${password.key.valid-time}")
    private Long validTime;

    private final PublicKeyService publicKeyService;

    @Autowired
    public PublicKeyController(PublicKeyService publicKeyService) {
        this.publicKeyService = publicKeyService;
    }

    @GetMapping("/public_key")
    public Map<String, String> getPublicKey() {
        Map<String, String> responseBody = new LinkedHashMap<>();
        responseBody.put("public_key", publicKeyService.getPublicKey());
        responseBody.put("expiration",System.currentTimeMillis()+validTime.toString());
        return responseBody;
    }
}
