package com.udacity.stockhawk.mock;
import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.util.RedirectableRequest;

/**
 * Created by mgajewski on 2017-05-18.
 */

public class MockHistoricData {

    public static List<HistoricalQuote> getHistory() throws IOException {

        List<HistoricalQuote> result = new ArrayList<HistoricalQuote>();

        // Get Faked data from github
        String url = "https://gist.githubusercontent.com/popnfresh234/4417d224b4bd6da8127b82774646d016/raw/eabe269d494152872607ff0183ab6d0f53029675/Sock%2520Hawk%2520Dummy%2520Historical%2520Data";
        YahooFinance.logger.log(Level.INFO, ("Sending request: " + url));

        URL request = new URL(url);
        RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
        redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
        redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
        URLConnection connection = redirectableRequest.openConnection();

        InputStreamReader is = new InputStreamReader(connection.getInputStream());

        JsonReader jsonReader = new JsonReader(is);

        jsonReader.beginObject(); //root
        jsonReader.nextName();
        jsonReader.beginObject(); //query
        while(jsonReader.hasNext()) {
            String results = jsonReader.nextName();
            if (Objects.equals(results, "results")) {
                jsonReader.beginObject(); //results
                String quote = jsonReader.nextName();
                if (Objects.equals(quote, "quote")) {
                    jsonReader.beginArray();  //quote
                    while (jsonReader.hasNext()) {
                        result.add(parseHistoricalQuote(jsonReader));
                    }
                    jsonReader.endArray();
                }
                jsonReader.endObject();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        jsonReader.endObject();

        return result;
    }



    private static HistoricalQuote parseHistoricalQuote(JsonReader jsonReader) throws IOException {
        String Symbol = "";
        String Date = "";
        String Open = "";
        String High = "";
        String Low = "";
        String Close = "";
        String Volume = "";
        String Adj_Close = "";

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (Objects.equals(name, "Symbol")) {
                Symbol = jsonReader.nextString();
            } else if (Objects.equals(name, "Date")) {
                Date = jsonReader.nextString();
            } else if (Objects.equals(name, "Open")) {
                Open = jsonReader.nextString();
            } else if (Objects.equals(name, "High")) {
                High = jsonReader.nextString();
            } else if (Objects.equals(name, "Low")) {
                Low = jsonReader.nextString();
            } else if (Objects.equals(name, "Close")) {
                Close = jsonReader.nextString();
            } else if (Objects.equals(name, "Volume")) {
                Volume = jsonReader.nextString();
            } else if (Objects.equals(name, "Adj_Close")) {
                Adj_Close = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        return new HistoricalQuote(Symbol,
                Utils.parseHistDate(Date),
                Utils.getBigDecimal(Open),
                Utils.getBigDecimal(Low),
                Utils.getBigDecimal(High),
                Utils.getBigDecimal(Close),
                Utils.getBigDecimal(Adj_Close),
                Utils.getLong(Volume)
        );
    }
}
