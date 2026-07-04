package serviceLibrary.proxies;

import org.springframework.cloud.openfeign.FeignClient;

import serviceLibrary.services.cryptoWallet.CryptoWalletService;

@FeignClient(name = "crypto-wallet")
public interface CryptoWalletProxy extends CryptoWalletService {
}