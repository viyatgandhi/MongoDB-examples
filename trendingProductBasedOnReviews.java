import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.mongodb.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.text.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/*
 * Program finds trending products in each city based on maximum number of ratings with 5 star
 */

public class trendingProductBasedOnReviews extends HttpServlet {
MongoClient mongo;

public void init() throws ServletException {
        mongo = new MongoClient("localhost", 27017);
}


public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter output = response.getWriter();

        String pageHeading = "<h1>Trending Products in City</h1>";

        String myPageTop = "<!DOCTYPE html>" + "<html lang=\"en\">"
                           + "<head>	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                           + "<title>Game Speed</title>"
                           + "<link rel=\"stylesheet\" href=\"styles.css\" type=\"text/css\" />"
                           + "</head>"
                           + "<body>"
                           + "<div id=\"container\">"
                           + "<header>"
                           + "<h1><a href=\"/\">GameSpeed<span></span></a></h1><h2>Assignment 2 - Viyat Gandhi</h2>"
                           + "</header>"
                           + "<nav>"
                           + "<ul>"
                           + "<li class=\"\"><a href=\"index\">Home</a></li>"
                           + "<li class = \"start selected\"><a href=\"DataAnalytics.html\">Data Analytics</a></li>"
                           + "</ul>"
                           + "</nav>"
                           + "<div id=\"body\">"
                           + "<section id=\"review-content\">"
                           + "<article>"
                           + "<h3style=\"color:#DE2D3A;font-weight:700;\">" +pageHeading + "</h3>";

        output.println(myPageTop);

        DB db = mongo.getDB("CustomerReviews");
        DBCollection myReviews = db.getCollection("myReviews");

        try{


                DBObject match = null;
                BasicDBObject matchFields = new BasicDBObject();
                DBObject group1Fields = null;
                DBObject group1 = null;
                DBObject group2Fields = null;
                DBObject group2 = null;
                DBObject projectFields = null;
                DBObject project = null;
                AggregationOutput aggregate = null;

                matchFields.put("reviewRating",5);

                match= new BasicDBObject("$match", matchFields);

                group1Fields = new BasicDBObject();

                HashMap<String,Object> group1ByInput = new HashMap<String,Object>();
                group1ByInput.put("City","$retailorCity");
                group1ByInput.put("Product","$productName");
                //group1ByInput.put("Review","$reviewText");

                group1Fields.put("_id",new BasicDBObject(group1ByInput));
                group1Fields.put("count", new BasicDBObject("$sum", 1));


                group1 = new BasicDBObject("$group", group1Fields);

                group2Fields = new BasicDBObject();

                HashMap<String,Object> group2ByInput = new HashMap<String,Object>();
                group2ByInput.put("product","$_id.Product");
                //group2ByInput.put("review","$_id.Review");
                group2ByInput.put("innercount","$count");


                group2Fields.put("_id","$_id.City");
                group2Fields.put("products",new BasicDBObject("$push",group2ByInput));
                group2Fields.put("outercount", new BasicDBObject("$max","$count"));

                group2 = new BasicDBObject("$group", group2Fields);

                projectFields = new BasicDBObject("_id", 0);
                projectFields.put("CountLast", "$outercount");
                projectFields.put("CityFinal", "$_id");
                projectFields.put("ProductCount", "$products.innercount");
                projectFields.put("ProductName","$products.product");

                project = new BasicDBObject("$project", projectFields);

                aggregate = myReviews.aggregate(match,group1,group2,project);
                // two group By are used , second is used to find individual review count inside each group by

                int rowCount = 0;
                int reviewCount=0;
                String tableData = " ";
                String pageContent = " ";
                int maxCount=0;

                for (DBObject result : aggregate.results()) {
                        BasicDBObject bobj = (BasicDBObject) result;
                        BasicDBList productNameList = (BasicDBList)bobj.get("ProductName");
                        BasicDBList productCountList = (BasicDBList)bobj.get("ProductCount");

                        rowCount++;

                        maxCount=bobj.getInt("CountLast");

                        tableData = "<tr><td><h3>Max Review Count: "+bobj.getString("CountLast")+"</h3></td>&nbsp"
                                    + "<td><h3>City: "+bobj.getString("CityFinal")+"</h3></td><br>"
                                    + "<td><h3>Rating: 5 </h3></td></tr>";

                        pageContent = "<table class = \"query-table\">"+tableData+"</table>";
                        output.println(pageContent);

                        while (reviewCount < productCountList.size()) {

                                int innercount=(Integer)productCountList.get(reviewCount);

                                if (maxCount==innercount) {
                                        tableData = "<tr rowspan = \"3\"><td> Product: "+productNameList.get(reviewCount)+"</br>"
                                                    + "Review Count: "+productCountList.get(reviewCount)+"</td></tr>";


                                        pageContent = "<table class = \"query-table\">"+tableData+"</table>";
                                        output.println(pageContent);

                                }

                                reviewCount++;
                        }

                        reviewCount=0;

                }

                if(rowCount == 0) {
                        pageContent = "<h1>No Data Found</h1>";
                        output.println(pageContent);
                }

        }catch (MongoException e) {
                e.printStackTrace();
        }

        String myPageBottom = "</article>"
                              + "</section>"
                              + "<div class=\"clear\"></div>"
                              + "</div>"
                              + "<footer>"
                              + "<div class=\"footer-bottom\">"
                              + "<p>CSP 595 - Enterprise Web Application - Assignment2 - Viyat Gandhi</p>"
                              + "</div>"
                              + "</footer>"
                              + "</div>"
                              + "</body>"
                              + "</html>";

        output.println(myPageBottom);

}
}
