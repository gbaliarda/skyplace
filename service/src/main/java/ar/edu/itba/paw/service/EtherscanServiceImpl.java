package ar.edu.itba.paw.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EtherscanServiceImpl implements EtherscanService {

    private final Dotenv env = Dotenv.load();
    private final String ETHERSCAN_URL = "https://api.etherscan.io/api";
    private final String GOERLI_URL = "https://api-goerli.etherscan.io/api";
    private final boolean USE_TESTNET = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(EtherscanServiceImpl.class);

    private boolean isTxSuccess(String tx) {
        HttpURLConnection con = null;
        try {
            Map<String, String> args = new HashMap<>();
            args.put("module", "transaction");
            args.put("action", "getstatus");
            args.put("txhash", tx);
            args.put("apikey", env.get("ETHERSCAN_KEY"));
            con = getConnection(args);
            if(con.getResponseCode() != 200) {
                con.disconnect();
                return false;
            }
            Reader reader = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
            JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
            JsonObject res = (JsonObject) obj.get("result");
            con.disconnect();
            return res.get("isError").getAsInt() == 0;
        } catch(IOException e) {
            LOGGER.error("Error connecting to API: {}",e.getMessage());
            if(con != null)
                con.disconnect();
            return false;
        } catch (Exception e) {
            LOGGER.error("General API error {}",e.getMessage());
            if(con != null)
                con.disconnect();
            return false;
        }
    }

    private HttpURLConnection getConnection(Map<String,String> args) throws IOException {
        URL url;
        String API_URL = USE_TESTNET ? GOERLI_URL : ETHERSCAN_URL;
        StringBuilder argString = new StringBuilder(API_URL).append("?");
        for(Map.Entry<String,String> entry:args.entrySet())
            argString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        if(args.entrySet().size() > 0)
            argString.deleteCharAt(argString.length()-1);

        url = new URL(argString.toString());


        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        return con;
    }

    private JsonElement getResult(HttpURLConnection con) throws IOException {
        if(con.getResponseCode() != 200)
            return null;

        InputStream input = con.getInputStream();
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        return new Gson().fromJson(reader, JsonObject.class);
    }

    @Override
    public boolean isTransactionValid(String txHash, String walletBuyer, String walletSeller, BigDecimal price) {
        if(!isTxSuccess(txHash))
            return false;
        HttpURLConnection con;
        try {
            Map<String,String> args = new HashMap<>();
            args.put("module","proxy");
            args.put("action","eth_getTransactionByHash");
            args.put("txhash",txHash);
            args.put("apikey",env.get("ETHERSCAN_KEY"));
            con = getConnection(args);
        } catch(IOException e) {
            LOGGER.error("Error connecting to API: {}",e.getMessage());
            return false;
        }
        JsonObject obj, res;
        try {
            obj = (JsonObject) getResult(con);
            if(obj == null) {
                con.disconnect();
                return false;
            }
            res = (JsonObject) obj.get("result");
        } catch (IOException e) {
            LOGGER.error("Error adding arguments: {}",e.getMessage());
            con.disconnect();
            return false;
        } catch (ClassCastException e) {
            LOGGER.error("Error getting JSON: {}",e.getMessage());
            con.disconnect();
            return false;
        }


        String from, to, valueInWei;
        BigDecimal value;
        try {
            from = res.get("from").getAsString();
            to = res.get("to").getAsString();
            valueInWei = res.get("value").getAsString();
            value = weiToEth(valueInWei).stripTrailingZeros();
        } catch (NullPointerException e) {
            LOGGER.error("Error parsing JSON: {}",e.getMessage());
            con.disconnect();
            return false;
        }

        boolean valid = from.equalsIgnoreCase(walletBuyer) && to.equalsIgnoreCase(walletSeller) && value.compareTo(price) == 0;

        con.disconnect();
        return valid;
    }

    private BigDecimal weiToEth(String wei) {
        BigInteger weiToBigInt = new BigInteger(wei.substring(2), 16);
        BigDecimal weiToBigDecimal = new BigDecimal(weiToBigInt);
        BigDecimal ethToWeiConversion = BigDecimal.valueOf(Math.pow(10,18));
        return weiToBigDecimal.divide(ethToWeiConversion, 18, RoundingMode.CEILING);
    }
}
