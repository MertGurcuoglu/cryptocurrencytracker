package com.mertgurcuoglu.currencytracking.service;

import com.mertgurcuoglu.currencytracking.dto.CoinGeckoMarketsResponse;
import com.mertgurcuoglu.currencytracking.dto.CurrencyValueDto;
import com.mertgurcuoglu.currencytracking.dto.SsePayload;
import com.mertgurcuoglu.currencytracking.model.Currency;
import com.mertgurcuoglu.currencytracking.model.Favorite;
import com.mertgurcuoglu.currencytracking.repository.CurrencyRepository;
import com.mertgurcuoglu.currencytracking.repository.FavoriteRepository;
import com.mertgurcuoglu.currencytracking.exception.CurrencyNotFoundException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class CurrencyTracking {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyTracking.class);
    private static final List<String> TRACKED_COIN_IDS = Arrays.asList(
            
            "bitcoin", "ethereum", "tether", "binancecoin", "solana", "ripple", "usd-coin",
            "cardano", "dogecoin", "shiba-inu", "avalanche-2", "tron", "polkadot", "chainlink",
            
            "the-open-network", "polygon", "litecoin", "near", "bitcoin-cash", "uniswap", "internet-computer",
            "stellar", "leo-token", "okb", "monero", "cosmos", "crypto-com-chain", "dai",

            "ethereum-classic", "hedera-hashgraph", "filecoin", "lido-dao", "aptos", "quant-network", "vechain",
            "immutable-x", "optimism", "arbitrum", "render-token", "the-graph", "fantom", "algorand",
    
            "stacks", "eos", "aave", "bittensor", "injective-protocol", "flow", "kaspa",
            "multiversx-egld", "synthetix-network-token", "the-sandbox", "tezos", "mana", "neo", "chiliz",

            "gala", "kucoin-shares", "axie-infinity", "iota", "ecash", "bitget-token", "klay-token",
            "gnosis", "pax-gold", "zcash", "huobi-token", "conflux-token", "curve-dao-token", "mina-protocol",

            "bittorrent", "trust-wallet-token", "pepe", "bonk", "floki", "casper-network", "rocket-pool",
            "arweave", "thorchain", "sui", "pancakeswap-token", "gatechain-token", "dydx", "worldcoin",

            "singularitynet", "jasmycoin", "gmx", "pyth-network", "flare-networks", "1inch", "enjincoin",
            "loopring", "basic-attention-token", "convex-finance", "ontology", "zilliqa", "wemix-token", "kava",
            "fetch-ai", "tether-gold"
    );


    private final FavoriteRepository favoriteRepository; 

    private final CurrencyRepository currencyRepository;
    private final RestTemplate restTemplate;
    private final Random random = new Random();
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isFetchingLiveRates = new AtomicBoolean(false);

    public CurrencyTracking(CurrencyRepository currencyRepository,
                            RestTemplate restTemplate, FavoriteRepository favoriteRepository) {
        this.currencyRepository = currencyRepository;
        this.restTemplate = restTemplate;
        this.favoriteRepository = favoriteRepository; 

    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        this.emitters.add(emitter);
        try {
            SsePayload initialPayload = new SsePayload("SİMÜLASYON", getCurrencyDtos());
            emitter.send(SseEmitter.event().name("init").data(initialPayload));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    private void pushUpdate(SsePayload payload) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("update").data(payload));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
    
    private List<CurrencyValueDto> getCurrencyDtos() {
        return currencyRepository.findAll().stream()
                .map(c -> new CurrencyValueDto(
                        c.getId(), c.getFullName(), c.getSymbol(), c.getCurrentPrice(), c.getLogoUrl(),
                        c.getMarketCap(), c.getTotalVolume(), c.getPriceChangePercentage24h(), c.getCirculatingSupply()))
                .collect(Collectors.toList());
    }
    public List<CurrencyValueDto> getAllCurrencyValues() {
        return getCurrencyDtos();
    }
    @Scheduled(fixedRate = 20000)
    public void sendHeartbeat() {
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (IOException e) { 

             }
        });
    }

    
    public Currency create(Currency currency) {
        logger.info("yeni para birimi oluşturuluyor: {}", currency.getSymbol());
        currency.setId(null);
        return currencyRepository.save(currency);
    }

    public Currency update(String id, Currency updatedCurrency) {
        logger.info("{} id'li para birimi güncelleniyor", id);
        return currencyRepository.findById(id)
                .map(existingCurrency -> {
                    existingCurrency.setFullName(updatedCurrency.getFullName());
                    existingCurrency.setSymbol(updatedCurrency.getSymbol());
                    existingCurrency.setMaxSupply(updatedCurrency.getMaxSupply());
                    return currencyRepository.save(existingCurrency);
                })
                .orElseThrow(() -> new CurrencyNotFoundException("para birimi bulunamadı: " + id));
    }
   
    public void delete(String id) {
        logger.info("{} id'li para birimi siliniyor", id);
        if (!currencyRepository.existsById(id)) {
            throw new CurrencyNotFoundException("silinecek para birimi bulunamadı: " + id);
        }
        currencyRepository.deleteById(id);
    }

    public Currency getById(String id) {
        logger.info("{} id'li para birimi aranıyor", id);
        return currencyRepository.findById(id)
                .orElseThrow(() -> new CurrencyNotFoundException("para birimi bulunamadı: " + id));
    }
    public List<Currency> getAll() {
        logger.info("tüm para birimleri listeleniyor");
        return currencyRepository.findAll();
    }
    @PostConstruct
    public void initializeBaseCurrencies() {
        if (currencyRepository.count() == 0) {
            logger.info("veritabanı boş, coingecko'dan başlangıç verileri çekiliyor");
            fetchAndSaveMarketData();
        } else {
            logger.info("veritabanı zaten dolu, başlangıç verileri atlanıyor");
        }
    }
    
    @Scheduled(fixedRate = 60000)
    public void fetchRealtimeRates() {
        logger.info("anlık piyasa verileri çekiliyor");
        fetchAndSaveMarketData();
    }

    private void fetchAndSaveMarketData() {
        if (!isFetchingLiveRates.compareAndSet(false, true)) {
            return;
        }
        try {
            String ids = String.join(",", TRACKED_COIN_IDS);
            String url = String.format("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=%s", ids);

            ResponseEntity<List<CoinGeckoMarketsResponse>> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            
            List<CoinGeckoMarketsResponse> markets = responseEntity.getBody();
            if (markets != null) {
                for (CoinGeckoMarketsResponse marketData : markets) {
                    Currency currency = currencyRepository.findBySymbol(marketData.getId())
                                            .orElse(new Currency());

                    currency.setFullName(marketData.getName());
                    currency.setSymbol(marketData.getId());
                    currency.setLogoUrl(marketData.getImage());
                    currency.setCurrentPrice(marketData.getCurrentPrice());
                    currency.setMarketCap(marketData.getMarketCap());
                    currency.setTotalVolume(marketData.getTotalVolume());
                    currency.setPriceChangePercentage24h(marketData.getPriceChangePercentage24h());
                    currency.setCirculatingSupply(marketData.getCirculatingSupply());
                    currency.setMaxSupply(marketData.getMaxSupply());
                    
                    currencyRepository.save(currency);
                }
                logger.info("{} adet para birimi güncellendi/eklendi", markets.size());
                pushUpdate(new SsePayload("CANLI", getCurrencyDtos()));
            }
        } catch (Exception e) {
            logger.error("piyasa verileri çekilirken hata oluştu: {}", e.getMessage());
        } finally {
            isFetchingLiveRates.set(false);
        }
    }
    @Scheduled(fixedRate = 1500)
    public void applyMicroFluctuations() { 
        if (isFetchingLiveRates.get()) {
            return;
        }
        List<Currency> currentLiveCurrencies = currencyRepository.findAll();
        List<CurrencyValueDto> fluctuatedDtos = currentLiveCurrencies.stream().map(c -> {
            double currentPrice = c.getCurrentPrice();
            if (currentPrice > 0) {
                double fluctuation = 0;
                if (random.nextInt(100) < 5) { 
                    fluctuation = (currentPrice * 0.005) * (random.nextDouble() - 0.5);
                } else {
                    if (currentPrice < 1.0) { fluctuation = (currentPrice * 0.002) * (random.nextDouble() - 0.5); }
                    else if (currentPrice < 100) { fluctuation = (currentPrice * 0.0005) * (random.nextDouble() - 0.5); }
                    else { fluctuation = (currentPrice * 0.00015) * (random.nextDouble() - 0.5); }
                }
                currentPrice += fluctuation;
            }
            return new CurrencyValueDto(c.getId(), c.getFullName(), c.getSymbol(), currentPrice, c.getLogoUrl(),
                    c.getMarketCap(), c.getTotalVolume(), c.getPriceChangePercentage24h(), c.getCirculatingSupply());
        }).collect(Collectors.toList());
        pushUpdate(new SsePayload("SİMÜLASYON", fluctuatedDtos));
    }

    public List<String> getUserFavoriteCurrencyIds(String userId) {
    return favoriteRepository.findByUserId(userId)
            .stream()
            .map(Favorite::getCurrencyId)
            .collect(Collectors.toList());
}

public void addFavorite(String userId, String currencyId) {
    if (!favoriteRepository.findByUserIdAndCurrencyId(userId, currencyId).isPresent()) {
        Favorite favorite = new Favorite(null, userId, currencyId);
        favoriteRepository.save(favorite);
        logger.info("Kullanıcı '{}' için favori eklendi: {}", userId, currencyId);
    }
}

public void removeFavorite(String userId, String currencyId) {
    favoriteRepository.deleteByUserIdAndCurrencyId(userId, currencyId);
    logger.info("Kullanıcı '{}' için favori silindi: {}", userId, currencyId);
}
    public List<String> getUserIdsByFavoriteCurrency(String currencyId) {
        return favoriteRepository.findByCurrencyId(currencyId)
                .stream() 
                .map(Favorite::getUserId) 
                .collect(Collectors.toList()); 
    }
}