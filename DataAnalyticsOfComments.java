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
import java.util.Map;
import java.util.Set;

/*
 * Below program extract reviews/comments left on website as per specfication and input by the user.
 * Output is number of reviews as per various parameter selected by user from input form.
 */

public class DataAnalyticsOfComments extends HttpServlet {

MongoClient mongo;

boolean groupByShowHighestPrice=false;

public void init() throws ServletException {
								mongo = new MongoClient("localhost", 27017);
}

public void doPost(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException {

								response.setContentType("text/html");

								PrintWriter output = response.getWriter();

								DB db = mongo.getDB("CustomerReviews");

								DBCollection myReviews = db.getCollection("myReviews");

								BasicDBObject query = new BasicDBObject();

								try {

																// Get the form data
																String productNameFetch = request.getParameter("productName");
																String productName="ALL_PRODUCTS";

																if (productNameFetch.equals("XBox_Original")) {
																								productName = "X Box Original";
																}else if (productNameFetch.equals("XBox_360")) {
																								productName = "X Box 360";
																}else if (productNameFetch.equals("XBox_One")) {
																								productName = "X Box One";
																}else if (productNameFetch.equals("PlayStation_2") ) {
																								productName = "PlayStation 2";
																}else if (productNameFetch.equals("PlayStation_3")) {
																								productName = "PlayStation 3";
																}else if (productNameFetch.equals("PlayStation_4") ) {
																								productName = "PlayStation 4";
																}else if (productNameFetch.equals("wii_Original")) {
																								productName = "Wii Original";
																}else if (productNameFetch.equals("wiiU")) {
																								productName = "Wii U";
																}else if (productNameFetch.equals("Destiny") ) {
																								productName = "Destiny";
																}else if (productNameFetch.equals("NBA2K13")) {
																								productName = "NBA 2013";
																}else if (productNameFetch.equals("NBALive_16") ) {
																								productName = "NBA Live 16";
																}

																int productPrice = Integer.parseInt(request.getParameter("productPrice"));
																String retailerZip = request.getParameter("retailerZip");
																String retailerCity = request.getParameter("retailerCity");
																int reviewRating = Integer.parseInt(request.getParameter("reviewRating"));
																String productCategory = request.getParameter("productCategory");
																String retailerName = request.getParameter("retailerName");
																String retailerState = request.getParameter("retailerState");
																String productSale = request.getParameter("productSale");
																String manuName = request.getParameter("manuName");
																String manuRebate = request.getParameter("manuRebate");
																String userID = request.getParameter("userID");
																int userAge = Integer.parseInt(request.getParameter("userAge"));
																String userGender = request.getParameter("userGender");
																String userOccupation = request.getParameter("userOccupation");

																// convert string date to type date for comparision
																String reviewDateFetch = request.getParameter("reviewDate");
																Date reviewDate =  new Date();


																try {
																								DateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd");
																								reviewDate = formatdate.parse(reviewDateFetch);

																} catch (ParseException e) {
																								e.printStackTrace();
																}



																String compareRating = request.getParameter("compareRating");
																String comparePrice = request.getParameter("comparePrice");
																String compareAge = request.getParameter("compareAge");
																String compareDate = request.getParameter("compareDate");
																//String returnValueDropdown = request.getParameter("returnValue");
																String groupByDropdown = request.getParameter("groupByDropdown");
																String groupByHighestPriceOnly = request.getParameter("groupByShowHighestPrice");

																boolean groupByreturn=false;

																String returnValueDropdown = request.getParameter("returnValue");
																if(!returnValueDropdown.equals("ALL")) {
																								groupByreturn=true;
																}

																// get sort by value if retrun selected
																String returnSortValueDropdown = request.getParameter("returnValueTop");

																//Boolean flags to check the filter settings
																boolean noFilter = false;
																boolean filterByProduct = false;
																boolean filterByPrice = false;
																boolean filterByZip = false;
																boolean filterByCity = false;
																boolean filterByRating = false;
																boolean filterByProductCategory = false;
																boolean filterRetailerName = false;
																boolean filterByRetailerState = false;
																boolean filterByProductSale = false;
																boolean filterBymanuName= false;
																boolean filterBymanuRebate= false;
																boolean filterByUserID= false;
																boolean filterByUserAge = false;
																boolean filterByUserGender = false;
																boolean filterByUserOccupation = false;
																boolean filterByReviewDate = false;

																// group by settings
																boolean groupByFilter= false; // to set match fields

																boolean groupBy = false;
																boolean groupByCity = false;
																boolean groupByProduct = false;
																boolean groupByZipCode = false;

																boolean groupByState = false;
																boolean groupByProductCategory = false;
																boolean groupByRetailerName = false;
																boolean groupByPrice=false;
																boolean groupByReviewRating = false;
																boolean groupByProductSale = false;
																boolean groupBymanuName= false;
																boolean groupBymanuRebate= false;
																boolean groupByByUserID= false;
																boolean groupByUserAge = false;
																boolean groupByUserGender = false;
																boolean groupByUserOccupation = false;
																boolean groupByReviewDate = false;

																boolean countOnly = false;

																//boolean groupByShowHighestPrice = false;

																//Get the filters selected
																//Filter - Simple Search
																String[] filters = request.getParameterValues("queryCheckBox");
																//Filters - Group By
																String[] extraSettings = request.getParameterValues("extraSettings");

																DBCursor dbCursor = null;
																AggregationOutput aggregateData = null;

																//Check for extra settings(Grouping Settings)
																if(extraSettings != null) {
																								//User has selected extra settings

																								for(int x = 0; x <extraSettings.length; x++) {
																																switch (extraSettings[x]) {
																																case "COUNT_ONLY":
																																								//Not implemented functionality to return count only
																																								countOnly = true;
																																								break;
																																case "GROUP_BY":
																																								groupBy = true;
																																								//Can add more grouping conditions here
																																								if(groupByDropdown.equals("GROUP_BY_CITY")) {
																																																groupByCity = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_PRODUCT")) {
																																																groupByProduct = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_ZIPCODE")) {
																																																groupByZipCode = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_STATE")) {
																																																groupByState = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_PROCAT")) {
																																																groupByProductCategory = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_PRICE")) {
																																																groupByPrice = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_RATING")) {
																																																groupByReviewRating = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_SALE")) {
																																																groupByProductSale = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_MNAME")) {
																																																groupBymanuName = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_MREBATE")) {
																																																groupBymanuRebate = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_USERID")) {
																																																groupByByUserID = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_AGE")) {
																																																groupByUserAge = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_GENDER")) {
																																																groupByUserGender = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_OCCU")) {
																																																groupByUserOccupation = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_DATE")) {
																																																groupByReviewDate  = true;
																																								}else if(groupByDropdown.equals("GROUP_BY_RNAME")) {
																																																groupByRetailerName = true;
																																								}

																																								if(groupByHighestPriceOnly!=null) {
																																																groupByShowHighestPrice = true;
																																								}
																																								break;
																																}
																								}
																}

																//Check the main filters only if the 'groupBy' option is not selected
																if(filters != null && groupBy != true) {
																								for (int i = 0; i < filters.length; i++) {
																																//Check what all filters are ON
																																//Build the query accordingly
																																switch (filters[i]) {
																																case "productName":
																																								filterByProduct = true;
																																								if(!productName.equals("ALL_PRODUCTS")) {
																																																query.put("productName", productName);
																																								}
																																								break;

																																case "productPrice":
																																								filterByPrice = true;
																																								if (comparePrice.equals("EQUALS_TO")) {
																																																query.put("productPrice", productPrice);
																																								}else if(comparePrice.equals("GREATER_THAN")) {
																																																query.put("productPrice", new BasicDBObject("$gt", productPrice));
																																								}else if(comparePrice.equals("LESS_THAN")) {
																																																query.put("productPrice", new BasicDBObject("$lt", productPrice));
																																								}
																																								break;

																																case "retailerZip":
																																								filterByZip = true;
																																								query.put("retailerZip", retailerZip);
																																								break;

																																case "retailerCity":
																																								filterByCity = true;
																																								if(!retailerCity.equals("All") && !groupByCity) {
																																																query.put("retailorCity", retailerCity);
																																								}
																																								break;

																																case "reviewRating":
																																								filterByRating = true;
																																								if (compareRating.equals("EQUALS_TO")) {
																																																query.put("reviewRating", reviewRating);
																																								}else if (compareRating.equals("GREATER_THAN")) {
																																																query.put("reviewRating", new BasicDBObject("$gt", reviewRating));
																																								} else if(compareRating.equals("LESS_THAN")) {
																																																query.put("reviewRating", new BasicDBObject("$lt", reviewRating));
																																								}
																																								break;

																																case "productCategory":
																																								filterByProductCategory = true;
																																								query.put("productCategory", productCategory);
																																								break;

																																case "retailerName":
																																								filterRetailerName = true;
																																								query.put("retailorName", retailerName);
																																								break;

																																case "retailerState":
																																								filterByRetailerState = true;
																																								query.put("retailorState", retailerState);
																																								break;

																																case "productSale":
																																								filterByProductSale = true;
																																								query.put("productOnSale", productSale);
																																								break;

																																case "manuName":
																																								filterBymanuName = true;
																																								query.put("manufacturerName", manuName);
																																								break;

																																case "manuRebate":
																																								filterBymanuRebate = true;
																																								query.put("manufacturerRebate", manuRebate);
																																								break;

																																case "userID":
																																								filterByUserID = true;
																																								query.put("userID", userID);
																																								break;

																																case "userAge":
																																								filterByUserAge = true;
																																								if (compareAge.equals("EQUALS_TO")) {
																																																query.put("userAge", userAge);
																																								}else if (compareAge.equals("GREATER_THAN")) {
																																																query.put("userAge", new BasicDBObject("$gt", userAge));
																																								} else if(compareAge.equals("LESS_THAN")) {
																																																query.put("userAge", new BasicDBObject("$lt", userAge));
																																								}
																																								break;

																																case "userGender":
																																								filterByUserGender = true;
																																								query.put("userGender", userGender);
																																								break;

																																case "userOccupation":
																																								filterByUserOccupation = true;
																																								query.put("userOccupation", userOccupation);
																																								break;

																																case "reviewDate":
																																								filterByReviewDate = true;
																																								if (compareDate.equals("EQUALS_TO")) {
																																																query.put("reviewDate", reviewDate);
																																								}else if (compareDate.equals("GREATER_THAN")) {
																																																query.put("reviewDate", new BasicDBObject("$gt", reviewDate));
																																								} else if(compareDate.equals("LESS_THAN")) {
																																																query.put("reviewDate", new BasicDBObject("$lt", reviewDate));
																																								}
																																								break;


																																default:
																																								//Show all the reviews if nothing is selected
																																								noFilter = true;
																																								break;
																																}
																								}
																}else{
																								//Show all the reviews if nothing is selected
																								noFilter = true;
																}


																//Construct the top of the page
																constructPageTop(output);

																// match fileds for aggregation
																DBObject match = null;
																BasicDBObject matchFields = new BasicDBObject(); // for match with group by
																BasicDBObject groupByQuery = new BasicDBObject(); // for group by with return selected

																//check filters only if group by is selected
																if(filters != null && groupBy == true) {
																								groupByFilter = true;
																								for (int i = 0; i < filters.length; i++) {
																																//Check what all filters are ON
																																//set the flags on basis of filters and put match in aggregate function
																																switch (filters[i]) {
																																case "productName":
																																								if(!productName.equals("ALL_PRODUCTS")) {
																																																matchFields.put("productName", productName);
																																								}
																																								break;

																																case "reviewRating":
																																								if (compareRating.equals("EQUALS_TO")) {
																																																matchFields.put("reviewRating",reviewRating);
																																								}else if (compareRating.equals("GREATER_THAN")) {
																																																matchFields.put("reviewRating", new BasicDBObject("$gt",reviewRating));
																																								} else if(compareRating.equals("LESS_THAN")) {
																																																matchFields.put("reviewRating", new BasicDBObject("$lt",reviewRating));
																																								}
																																								break;

																																case "reviewDate":
																																								if (compareDate.equals("EQUALS_TO")) {
																																																matchFields.put("reviewDate",reviewDate);
																																								}else if (compareDate.equals("GREATER_THAN")) {
																																																matchFields.put("reviewDate", new BasicDBObject("$gt", reviewDate));
																																								} else if(compareDate.equals("LESS_THAN")) {
																																																matchFields.put("reviewDate", new BasicDBObject("$lt", reviewDate));
																																								}
																																								break;

																																case "productPrice":
																																								if (comparePrice.equals("EQUALS_TO")) {
																																																matchFields.put("productPrice", productPrice);
																																																//groupByQuery.put("productPrice",productPrice);
																																								}else if(comparePrice.equals("GREATER_THAN")) {
																																																matchFields.put("productPrice", new BasicDBObject("$gt", productPrice));
																																								}else if(comparePrice.equals("LESS_THAN")) {
																																																matchFields.put("productPrice", new BasicDBObject("$lt", productPrice));
																																								}
																																								break;

																																case "retailerZip":
																																								matchFields.put("retailerZip", retailerZip);
																																								break;

																																case "retailerCity":
																																								if(!retailerCity.equals("All") && !groupByCity) {
																																																matchFields.put("retailorCity", retailerCity);
																																								}
																																								break;

																																case "productCategory":
																																								matchFields.put("productCategory", productCategory);
																																								break;

																																case "retailerName":
																																								matchFields.put("retailorName", retailerName);
																																								break;

																																case "retailerState":
																																								matchFields.put("retailorState", retailerState);
																																								break;

																																case "productSale":
																																								matchFields.put("productOnSale", productSale);
																																								break;

																																case "manuName":
																																								matchFields.put("manufacturerName", manuName);
																																								break;

																																case "manuRebate":
																																								matchFields.put("manufacturerRebate", manuRebate);
																																								break;

																																case "userID":
																																								matchFields.put("userID", userID);
																																								break;

																																case "userAge":
																																								if (compareAge.equals("EQUALS_TO")) {
																																																matchFields.put("userAge", userAge);
																																								}else if (compareAge.equals("GREATER_THAN")) {
																																																matchFields.put("userAge", new BasicDBObject("$gt", userAge));
																																								} else if(compareAge.equals("LESS_THAN")) {
																																																matchFields.put("userAge", new BasicDBObject("$lt", userAge));
																																								}
																																								break;

																																case "userGender":
																																								matchFields.put("userGender", userGender);
																																								break;

																																case "userOccupation":
																																								matchFields.put("userOccupation", userOccupation);
																																								break;

																																default:
																																								//Show all the reviews if nothing is selected
																																								noFilter = true;
																																								break;
																																}
																								}
																}else{
																								//Show all the reviews if nothing is selected
																								noFilter = true;
																}



																//check filters only if group by is selected with return value
																if(filters != null && groupBy == true && groupByreturn==true ) {

																								for (int i = 0; i < filters.length; i++) {
																																//Check what all filters are ON with group by
																																//set the flags on basis of filters and pass the query to return function
																																switch (filters[i]) {
																																case "productName":
																																								if(!productName.equals("ALL_PRODUCTS")) {
																																																groupByQuery.put("productName", productName);
																																								}
																																								break;

																																case "reviewRating":
																																								if (compareRating.equals("EQUALS_TO")) {
																																																groupByQuery.put("reviewRating",reviewRating);
																																								}else if (compareRating.equals("GREATER_THAN")) {
																																																groupByQuery.put("reviewRating", new BasicDBObject("$gt",reviewRating));
																																								} else if(compareRating.equals("LESS_THAN")) {
																																																groupByQuery.put("reviewRating", new BasicDBObject("$lt",reviewRating));
																																								}
																																								break;

																																case "reviewDate":
																																								if (compareDate.equals("EQUALS_TO")) {
																																																groupByQuery.put("reviewDate",reviewDate);
																																								}else if (compareDate.equals("GREATER_THAN")) {
																																																groupByQuery.put("reviewDate", new BasicDBObject("$gt", reviewDate));
																																								} else if(compareDate.equals("LESS_THAN")) {
																																																groupByQuery.put("reviewDate", new BasicDBObject("$lt", reviewDate));
																																								}
																																								break;

																																case "productPrice":
																																								if (comparePrice.equals("EQUALS_TO")) {
																																																groupByQuery.put("productPrice", productPrice);
																																								}else if(comparePrice.equals("GREATER_THAN")) {
																																																groupByQuery.put("productPrice", new BasicDBObject("$gt", productPrice));
																																								}else if(comparePrice.equals("LESS_THAN")) {
																																																groupByQuery.put("productPrice", new BasicDBObject("$lt", productPrice));
																																								}
																																								break;

																																case "retailerZip":
																																								groupByQuery.put("retailerZip", retailerZip);
																																								break;

																																case "retailerCity":
																																								if(!retailerCity.equals("All") && !groupByCity) {
																																																groupByQuery.put("retailorCity", retailerCity);
																																								}
																																								break;

																																case "productCategory":
																																								groupByQuery.put("productCategory", productCategory);
																																								break;

																																case "retailerName":
																																								groupByQuery.put("retailorName", retailerName);
																																								break;

																																case "retailerState":
																																								groupByQuery.put("retailorState", retailerState);
																																								break;

																																case "productSale":
																																								groupByQuery.put("productOnSale", productSale);
																																								break;

																																case "manuName":
																																								groupByQuery.put("manufacturerName", manuName);
																																								break;

																																case "manuRebate":
																																								groupByQuery.put("manufacturerRebate", manuRebate);
																																								break;

																																case "userID":
																																								groupByQuery.put("userID", userID);
																																								break;

																																case "userAge":
																																								if (compareAge.equals("EQUALS_TO")) {
																																																groupByQuery.put("userAge", userAge);
																																								}else if (compareAge.equals("GREATER_THAN")) {
																																																groupByQuery.put("userAge", new BasicDBObject("$gt", userAge));
																																								} else if(compareAge.equals("LESS_THAN")) {
																																																groupByQuery.put("userAge", new BasicDBObject("$lt", userAge));
																																								}
																																								break;

																																case "userGender":
																																								groupByQuery.put("userGender", userGender);
																																								break;

																																case "userOccupation":
																																								groupByQuery.put("userOccupation", userOccupation);
																																								break;

																																default:
																																								//Show all the reviews if nothing is selected
																																								noFilter = true;
																																								break;
																																}
																								}
																}

																//Run the query
																if(groupBy == true) {

																								int returnLimit = 0;
																								DBObject sort = new BasicDBObject();

																								if (groupByreturn) {

																																if (returnValueDropdown.equals("TOP_5")) {
																																								//Top 5 - Sorted by review rating
																																								returnLimit = 5;
																																								sort.put(returnSortValueDropdown,-1);
																																}else if (returnValueDropdown.equals("TOP_10")) {
																																								//Top 10 - Sorted by review rating
																																								returnLimit = 10;
																																								sort.put(returnSortValueDropdown,-1);
																																}else if (returnValueDropdown.equals("LATEST_5")) {
																																								//Latest 5 - Sort by date
																																								returnLimit = 5;
																																								sort.put("reviewDate",-1);
																																}else if (returnValueDropdown.equals("LATEST_10")) {
																																								//Latest 10 - Sort by date
																																								returnLimit = 10;
																																								sort.put("reviewDate",-1);
																																}
																								}
																								//Run the query using aggregate function
																								DBObject groupFields = null;
																								DBObject group = null;
																								DBObject projectFields = null;
																								DBObject project = null;
																								AggregationOutput aggregate = null;

																								if(groupByCity) {

																																String groupByReturnValue = "retailorCity";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																// for showing products of highest price only
																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$retailorCity");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("City", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group,project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								}else if(groupByProduct) {

																																String groupByReturnValue = "productName";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$productName");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));


																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Product", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByProductContent(aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								}
																								else if(groupByZipCode) {

																																String groupByReturnValue = "retailorZip";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$retailorZip");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Zipcode", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");


																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}


																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}


																								} else if (groupByState) {

																																String groupByReturnValue = "retailorState";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$retailorState");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("State", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");


																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByProductCategory) {

																																String groupByReturnValue = "productCategory";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$productCategory");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Product Category", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");


																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByPrice) {

																																String groupByReturnValue = "productPrice";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$productPrice");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Product Price", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");


																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByReviewRating) {

																																String groupByReturnValue = "reviewRating";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$reviewRating");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Review Rating", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");


																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByProductSale) {

																																String groupByReturnValue = "productSale";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$productOnSale");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Product Sale", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupBymanuName) {

																																String groupByReturnValue = "manuName";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$manufacturerName");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Manufacturer Name", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}



																								} else if (groupBymanuRebate) {

																																String groupByReturnValue = "manuRebate";


																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$manufacturerRebate");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("Manufacturer Rebate", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByByUserID) {

																																String groupByReturnValue = "userID";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$userID");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("UserID", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}


																								} else if (groupByUserAge) {

																																String groupByReturnValue = "UserAge";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$userAge");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("UserAge", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByUserGender) {

																																String groupByReturnValue = "UserGender";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$userGender");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("UserGender", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}



																								} else if (groupByUserOccupation) {

																																String groupByReturnValue = "UserOccupation";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$userOccupation");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("UserOccupation", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByReviewDate) {

																																String groupByReturnValue = "ReviewDate";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$reviewDate");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("ReviewDate", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								} else if (groupByRetailerName) {

																																String groupByReturnValue = "RetailerName";

																																projectFields = new BasicDBObject("_id", 0);

																																groupFields = new BasicDBObject("_id", 0);

																																if (groupByShowHighestPrice==true) {
																																								groupFields.put("maxPrice", new BasicDBObject("$max", "$productPrice"));
																																								projectFields.put("maxPrice", "$maxPrice");
																																}

																																groupFields.put("_id", "$retailorName");
																																groupFields.put("count", new BasicDBObject("$sum", 1));
																																groupFields.put("review", new BasicDBObject("$push", "$reviewText"));
																																groupFields.put("rating", new BasicDBObject("$push", "$reviewRating"));
																																groupFields.put("productName", new BasicDBObject("$push", "$productName"));
																																groupFields.put("price", new BasicDBObject("$push", "$productPrice"));

																																groupFields.put("date", new BasicDBObject("$push", "$reviewDate"));
																																groupFields.put("city", new BasicDBObject("$push", "$retailorCity"));
																																groupFields.put("zip", new BasicDBObject("$push", "$retailorZip"));
																																groupFields.put("rstate", new BasicDBObject("$push", "$retailorState"));
																																groupFields.put("rname", new BasicDBObject("$push", "$retailorName"));
																																groupFields.put("sale", new BasicDBObject("$push", "$productOnSale"));
																																groupFields.put("cat", new BasicDBObject("$push", "$productCategory"));
																																groupFields.put("manuName", new BasicDBObject("$push", "$manufacturerName"));
																																groupFields.put("manuRebate", new BasicDBObject("$push", "$manufacturerRebate"));
																																groupFields.put("age", new BasicDBObject("$push", "$userAge"));
																																groupFields.put("ID", new BasicDBObject("$push", "$userID"));
																																groupFields.put("gender", new BasicDBObject("$push", "$userGender"));
																																groupFields.put("occu", new BasicDBObject("$push", "$userOccupation"));

																																group = new BasicDBObject("$group", groupFields);

																																projectFields.put("RetailerName", "$_id");
																																projectFields.put("Review Count", "$count");
																																projectFields.put("Reviews", "$review");
																																projectFields.put("Rating", "$rating");
																																projectFields.put("Product", "$productName");
																																projectFields.put("Price", "$price");

																																projectFields.put("Date", "$date");
																																projectFields.put("RCity", "$city");
																																projectFields.put("Zip", "$zip");
																																projectFields.put("RState", "$rstate");
																																projectFields.put("RName", "$rname");
																																projectFields.put("Sale", "$sale");
																																projectFields.put("Cat", "$cat");
																																projectFields.put("MName", "$manuName");
																																projectFields.put("MRebate", "$manuRebate");
																																projectFields.put("Age", "$age");
																																projectFields.put("UName", "$ID");
																																projectFields.put("Gender", "$gender");
																																projectFields.put("Occu", "$occu");

																																project = new BasicDBObject("$project", projectFields);

																																if (groupByFilter==true) {
																																								match= new BasicDBObject("$match", matchFields);
																																								aggregate = myReviews.aggregate(match,group, project);
																																}else {
																																								aggregate = myReviews.aggregate(group, project);
																																}

																																//Construct the page content
																																if(returnLimit==0) {
																																								constructGroupByNormalContent(groupByReturnValue,aggregate, output, countOnly);
																																}else if(returnLimit>0) {
																																								constructGroupByAllTopContent(groupByQuery,groupByReturnValue,returnLimit,sort,aggregate, output, countOnly);
																																}

																								}

																}
																else{
																								//Check the return value selected
																								int returnLimit = 0;

																								//Create sort variable
																								DBObject sort = new BasicDBObject();

																								if (returnValueDropdown.equals("TOP_5")) {
																																//Top 5 - Sorted by review rating
																																returnLimit = 5;
																																sort.put(returnSortValueDropdown,-1);
																																dbCursor = myReviews.find(query).limit(returnLimit).sort(sort);
																								}else if (returnValueDropdown.equals("TOP_10")) {
																																//Top 10 - Sorted by review rating
																																returnLimit = 10;
																																sort.put(returnSortValueDropdown,-1);
																																dbCursor = myReviews.find(query).limit(returnLimit).sort(sort);
																								}else if (returnValueDropdown.equals("LATEST_5")) {
																																//Latest 5 - Sort by date
																																returnLimit = 5;
																																sort.put("reviewDate",-1);
																																dbCursor = myReviews.find(query).limit(returnLimit).sort(sort);
																								}else if (returnValueDropdown.equals("LATEST_10")) {
																																//Latest 10 - Sort by date
																																returnLimit = 10;
																																sort.put("reviewDate",-1);
																																dbCursor = myReviews.find(query).limit(returnLimit).sort(sort);
																								}else{
																																//Run the simple search query(default result)
																																dbCursor = myReviews.find(query);
																								}

																								//Construct the page content
																								constructDefaultContent(dbCursor, output, countOnly);
																}

																//Construct the bottom of the page
																constructPageBottom(output);


								} catch (MongoException e) {
																e.printStackTrace();
								}

}

public void constructPageTop(PrintWriter output){
								String pageHeading = "Query Result";
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
																											+ "<h2 style=\"color:#DE2D3A;font-weight:700;\">" +pageHeading + "</h2>";

								output.println(myPageTop);
}

public void constructPageBottom(PrintWriter output){
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

public void constructDefaultContent(DBCursor dbCursor, PrintWriter output, boolean countOnly){
								int count = 0;
								String tableData = " ";
								String pageContent = " ";

								if (countOnly==true) {

																HashMap<String,Integer> hmap = new HashMap<String,Integer>();

																while (dbCursor.hasNext()) {
																								count++;
																								BasicDBObject bobj = (BasicDBObject) dbCursor.next();

																								String product=bobj.getString("productName");
																								hmap.put(product,0);
																}

																int countOnlyValue=hmap.size();

																tableData =  "<tr><td><h3>Total Count: "+countOnlyValue+ "</h3></td></tr>";
																pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																output.println(pageContent);


																Set set = hmap.entrySet();
																Iterator iterator = set.iterator();
																while(iterator.hasNext()) {
																								Map.Entry mentry = (Map.Entry)iterator.next();
																								tableData = "<tr><td>Product is: "+ mentry.getKey() + "</td></tr>";
																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);
																}

								}

								if (countOnly!=true) {

																while (dbCursor.hasNext()) {
																								BasicDBObject bobj = (BasicDBObject) dbCursor.next();

																								Date reviewDateFetch= bobj.getDate("reviewDate");
																								DateFormat formatdate = new SimpleDateFormat("MM/dd/yyyy");
																								String reviewDate = formatdate.format(reviewDateFetch);

																								tableData =  "<tr><td>Name: <b>     " + bobj.getString("productName") + " </b></td></tr>"
																																				+ "<tr><td>Price:       " + bobj.getString("productPrice") + "</br>"
																																				+ "Retailer:            " + bobj.getString("retailorName") + "</br>"
																																				+ "Retailer Zipcode:    " + bobj.getString("retailorZip") + "</br>"
																																				+ "Retailer City:       " + bobj.getString("retailorCity") + "</br>"
																																				+ "Retailer State:      " + bobj.getString("retailorState") + "</br>"
																																				+ "Sale:                " + bobj.getString("productOnSale") + "</br>"
																																				+ "User ID:             " + bobj.getString("userID") + "</br>"
																																				+ "User Age:            " + bobj.getString("userAge") + "</br>"
																																				+ "User Gender:         " + bobj.getString("userGender") + "</br>"
																																				+ "User Occupation:     " + bobj.getString("userOccupation") + "</br>"
																																				+ "Manufacturer:        " + bobj.getString("manufacturerName") + "</br>"
																																				+ "Manufacturer Rebate: " + bobj.getString("manufacturerRebate") + "</br>"
																																				+ "Rating:              " + bobj.getString("reviewRating") + "</br>"
																																				+ "Date:                " + reviewDate+ "</br>"
																																				+ "Review Text:         " + bobj.getString("reviewText") + "</td></tr>";

																								count++;

																								output.println("<h3>"+count+"</h3>");
																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);
																}

								}

								//No data found
								if(count == 0) {
																pageContent = "<h1>No Data Found</h1>";
																output.println(pageContent);
								}

}

public void constructGroupByAllTopContent(BasicDBObject query, String groupByReturnValue,int returnLimit, DBObject sort,AggregationOutput aggregate, PrintWriter output, boolean countOnly){

								int rowCount = 0;
								int productCount = 0;
								String tableData = " ";
								String pageContent = " ";
								DBCursor dbCursor=null;
								String headerName=" ";
								//BasicDBObject query = new BasicDBObject();
								DB db = mongo.getDB("CustomerReviews");
								DBCollection myReviews = db.getCollection("myReviews");
								DateFormat formatdate = new SimpleDateFormat("MM/dd/yyyy");

								//	output.println("<h1> Grouped By - "+headerName+" </h1>");

								for (DBObject result : aggregate.results()) {
																BasicDBObject bobj = (BasicDBObject) result;


																if (groupByReturnValue.equals("retailorCity")) {
																								headerName=bobj.getString("City");
																								query.put("retailorCity",headerName);
																} else if (groupByReturnValue.equals("retailorZip")) {
																								headerName=bobj.getString("Zipcode");
																								query.put("retailorZip",headerName);
																} else if (groupByReturnValue.equals("productName")) {
																								headerName=bobj.getString("Product");
																								query.put("retailorZip",headerName);
																} else if (groupByReturnValue.equals("retailorState")) {
																								headerName=bobj.getString("State");
																								query.put("retailorState",headerName);
																}else if (groupByReturnValue.equals("productCategory")) {
																								headerName=bobj.getString("Product Category");
																								query.put("productCategory",headerName);
																}else if (groupByReturnValue.equals("productPrice")) {
																								int headerNameInt = bobj.getInt("Product Price");
																								query.put("productPrice",headerNameInt);
																								headerName=Integer.toString(headerNameInt);
																}else if (groupByReturnValue.equals("reviewRating")) {
																								int headerNameInt = bobj.getInt("Review Rating");
																								query.put("reviewRating",headerNameInt);
																								headerName = Integer.toString(headerNameInt);
																}else if (groupByReturnValue.equals("productSale")) {
																								headerName=bobj.getString("Product Sale");
																								query.put("productOnSale",headerName);
																}else if (groupByReturnValue.equals("manuName")) {
																								headerName=bobj.getString("Manufacturer Name");
																								query.put("manufacturerName",headerName);
																}else if (groupByReturnValue.equals("manuRebate")) {
																								headerName=bobj.getString("Manufacturer Rebate");
																								query.put("manufacturerRebate",headerName);
																}else if (groupByReturnValue.equals("userID")) {
																								headerName=bobj.getString("UserID");
																								query.put("userID",headerName);
																}else if (groupByReturnValue.equals("UserAge")) {
																								int headerNameInt=bobj.getInt("UserAge");
																								query.put("userAge",headerNameInt);
																								headerName=Integer.toString(headerNameInt);
																}else if (groupByReturnValue.equals("UserGender")) {
																								headerName=bobj.getString("UserGender");
																								query.put("userGender",headerName);
																}else if (groupByReturnValue.equals("UserOccupation")) {
																								headerName=bobj.getString("UserOccupation");
																								query.put("userOccupation",headerName);
																}else if (groupByReturnValue.equals("ReviewDate")) {
																								Date headerDate=bobj.getDate("ReviewDate");
																								query.put("reviewDate",headerDate);
																								headerName = formatdate.format(headerDate);
																}else if (groupByReturnValue.equals("RetailerName")) {
																								headerName=bobj.getString("RetailerName");
																								query.put("retailorName",headerName);
																}

																tableData = "<tr><td><h3>Group By Field: "+headerName+"</h3></td></tr>";
																pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																output.println(pageContent);

																dbCursor=myReviews.find(query).limit(returnLimit).sort(sort);

																while (dbCursor.hasNext()) {
																								rowCount++;
																								BasicDBObject obj = (BasicDBObject) dbCursor.next();

																								Date reviewDateFetch= obj.getDate("reviewDate");
																								String reviewDate = formatdate.format(reviewDateFetch);

																								tableData = "<tr rowspan = \"3\"><td> Product: "+obj.getString("productName")+"</br>"
																																				+   "Rating: "+obj.getString("reviewRating")+"</br>"
																																				+ "Review: "+obj.getString("reviewText")+"</br>"
																																				+ "Price: $"+obj.getString("productPrice")+"</br>"
																																				+ "User Age:"+obj.getString("userAge")+"</br>"
																																				+ "Review Date:"+reviewDate+"</br>"
																																				+ "User Gender: "+obj.getString("userGender")+"</br>"
																																				+ "User ID: "+obj.getString("userID")+"</br>"
																																				+ "Retailer Name: "+obj.getString("retailorName")+"</br>"
																																				+ "Retailer State: "+obj.getString("retailorState")+"</br>"
																																				+ "Retailer Zip: "+obj.getString("retailorZip")+"</br>"
																																				+ "Manufacturer Name: "+obj.getString("manufacturerName")+"</br>"
																																				+ "Manufacturer Rebate: "+obj.getString("manufacturerRebate")+"</br>"
																																				+ "Product On Sale "+obj.getString("productOnSale")+"</br>"
																																				+ "Product Category: "+obj.getString("productCategory")+"</br>"
																																				+ "User Occupation:"+obj.getString("userOccupation")+"</td></tr>";

																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);
																}



								}
								if(rowCount == 0) {
																pageContent = "<h1>No Data Found</h1>";
																output.println(pageContent);
								}

}

public void constructGroupByNormalContent(String groupByReturnValue,AggregationOutput aggregate, PrintWriter output, boolean countOnly){
								int rowCount = 0;
								int productCount = 0;
								String tableData = " ";
								String pageContent = " ";
								int maxPrice=0;
								String headerName=" ";
								DateFormat formatdate = new SimpleDateFormat("MM/dd/yyyy");

								//output.println("<h1> Grouped By - City </h1>");
								for (DBObject result : aggregate.results()) {
																BasicDBObject bobj = (BasicDBObject) result;
																BasicDBList productList = (BasicDBList) bobj.get("Product");
																BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
																BasicDBList rating = (BasicDBList) bobj.get("Rating");
																BasicDBList price = (BasicDBList) bobj.get("Price");

																BasicDBList rdate = (BasicDBList) bobj.get("Date");
																BasicDBList rcity = (BasicDBList) bobj.get("RCity");
																BasicDBList zip = (BasicDBList) bobj.get("Zip");
																BasicDBList state = (BasicDBList) bobj.get("RState");
																BasicDBList rname = (BasicDBList) bobj.get("RName");
																BasicDBList sale = (BasicDBList) bobj.get("Sale");
																BasicDBList cat = (BasicDBList) bobj.get("Cat");
																BasicDBList mname = (BasicDBList) bobj.get("MName");
																BasicDBList mrebate = (BasicDBList) bobj.get("MRebate");
																BasicDBList age = (BasicDBList) bobj.get("Age");
																BasicDBList uname = (BasicDBList) bobj.get("UName");
																BasicDBList gender = (BasicDBList) bobj.get("Gender");
																BasicDBList occu = (BasicDBList) bobj.get("Occu");




																if (groupByReturnValue.equals("retailorCity")) {
																								headerName=bobj.getString("City");
																} else if (groupByReturnValue.equals("retailorZip")) {
																								headerName=bobj.getString("Zipcode");
																} else if (groupByReturnValue.equals("productName")) {
																								headerName=bobj.getString("Product");
																} else if (groupByReturnValue.equals("retailorState")) {
																								headerName=bobj.getString("State");
																}else if (groupByReturnValue.equals("productCategory")) {
																								headerName=bobj.getString("Product Category");
																}else if (groupByReturnValue.equals("productPrice")) {
																								int headerNameInt = bobj.getInt("Product Price");
																								headerName=Integer.toString(headerNameInt);
																}else if (groupByReturnValue.equals("reviewRating")) {
																								int headerNameInt = bobj.getInt("Review Rating");
																								headerName = Integer.toString(headerNameInt);
																}else if (groupByReturnValue.equals("productSale")) {
																								headerName=bobj.getString("Product Sale");
																}else if (groupByReturnValue.equals("manuName")) {
																								headerName=bobj.getString("Manufacturer Name");
																}else if (groupByReturnValue.equals("manuRebate")) {
																								headerName=bobj.getString("Manufacturer Rebate");
																}else if (groupByReturnValue.equals("userID")) {
																								headerName=bobj.getString("UserID");
																}else if (groupByReturnValue.equals("UserAge")) {
																								int headerNameInt=bobj.getInt("UserAge");
																								headerName=Integer.toString(headerNameInt);
																}else if (groupByReturnValue.equals("UserGender")) {
																								headerName=bobj.getString("UserGender");
																}else if (groupByReturnValue.equals("UserOccupation")) {
																								headerName=bobj.getString("UserOccupation");
																}else if (groupByReturnValue.equals("RetailerName")) {
																								headerName=bobj.getString("RetailerName");
																}else if(groupByReturnValue.equals("ReviewDate")) {
																								Date reviewDateFetch= bobj.getDate("ReviewDate");
																								headerName = formatdate.format(reviewDateFetch);
																}

																rowCount++;

																if (groupByShowHighestPrice==true) {

																								maxPrice=bobj.getInt("maxPrice");

																								tableData = "<tr><td><h3>Group By Value: "+headerName+"</h3></td>&nbsp"
																																				+ "<td>Max Product Price: $"+bobj.getString("maxPrice")+"</td></tr>";

																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);

																								//Now print the products with the given review rating

																								if (countOnly!=true) {

																																while (productCount < productList.size()) {

																																								int productPrice=(Integer)price.get(productCount);


																																								if (productPrice==maxPrice) {
																																																tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(productCount)+"</br>"
																																																												+   "Rating: "+rating.get(productCount)+"</br>"
																																																												+ "Review: "+productReview.get(productCount)+"</br>"
																																																												+ "Price: $"+price.get(productCount)+"</br>"
																																																												+ "Review Date: "+formatdate.format(rdate.get(productCount))+"</br>"
																																																												+ "City: "+rcity.get(productCount)+"</br>"
																																																												+ "Zip: "+zip.get(productCount)+"</br>"
																																																												+ "State: "+state.get(productCount)+"</br>"
																																																												+ "Retailer Name: "+rname.get(productCount)+"</br>"
																																																												+ "Sale: "+sale.get(productCount)+"</br>"
																																																												+ "Category: "+cat.get(productCount)+"</br>"
																																																												+ "M Name: "+mname.get(productCount)+"</br>"
																																																												+ "M Reabte: "+mrebate.get(productCount)+"</br>"
																																																												+ "Age: "+age.get(productCount)+"</br>"
																																																												+ "UserID: "+uname.get(productCount)+"</br>"
																																																												+ "Gender: "+gender.get(productCount)+"</br>"
																																																												+ "Occupation: "+occu.get(productCount)+"</td></tr>";

																																																pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																																																output.println(pageContent);
																																								}
																																								productCount++;
																																}
																								}

																} else {

																								tableData = "<tr><td><h3>Group By Value: "+headerName+"</h3></td>&nbsp"
																																				+ "<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);

																								//Now print the products with the given review rating
																								if (countOnly!=true) {
																																while (productCount < productList.size()) {
																																								tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(productCount)+"</br>"
																																																				+   "Rating: "+rating.get(productCount)+"</br>"
																																																				+ "Review: "+productReview.get(productCount)+"</br>"
																																																				+ "Price: $"+price.get(productCount)+"</br>"
																																																				+ "Review Date: "+formatdate.format(rdate.get(productCount))+"</br>"
																																																				+ "City: "+rcity.get(productCount)+"</br>"
																																																				+ "Zip: "+zip.get(productCount)+"</br>"
																																																				+ "State: "+state.get(productCount)+"</br>"
																																																				+ "Retailer Name: "+rname.get(productCount)+"</br>"
																																																				+ "Sale: "+sale.get(productCount)+"</br>"
																																																				+ "Category: "+cat.get(productCount)+"</br>"
																																																				+ "M Name: "+mname.get(productCount)+"</br>"
																																																				+ "M Reabte: "+mrebate.get(productCount)+"</br>"
																																																				+ "Age: "+age.get(productCount)+"</br>"
																																																				+ "UserID: "+uname.get(productCount)+"</br>"
																																																				+ "Gender: "+gender.get(productCount)+"</br>"
																																																				+ "Occupation: "+occu.get(productCount)+"</td></tr>";

																																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																																								output.println(pageContent);

																																								productCount++;
																																}
																								}

																}
																//Reset product count
																productCount =0;

								}
								//No data found
								if(rowCount == 0) {
																pageContent = "<h1>No Data Found</h1>";
																output.println(pageContent);
								}
								//Reset value to false
								groupByShowHighestPrice=false;
}

public void constructGroupByProductContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
								int rowCount = 0;
								int reviewCount = 0;
								String tableData = " ";
								String pageContent = " ";
								int maxPrice=0;
								DateFormat formatdate = new SimpleDateFormat("MM/dd/yyyy");

								output.println("<h1> Grouped By - Products </h1>");
								for (DBObject result : aggregate.results()) {
																BasicDBObject bobj = (BasicDBObject) result;
																BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
																BasicDBList rating = (BasicDBList) bobj.get("Rating");
																BasicDBList price = (BasicDBList) bobj.get("Price");

																BasicDBList rdate = (BasicDBList) bobj.get("Date");
																BasicDBList rcity = (BasicDBList) bobj.get("RCity");
																BasicDBList zip = (BasicDBList) bobj.get("Zip");
																BasicDBList state = (BasicDBList) bobj.get("RState");
																BasicDBList rname = (BasicDBList) bobj.get("RName");
																BasicDBList sale = (BasicDBList) bobj.get("Sale");
																BasicDBList cat = (BasicDBList) bobj.get("Cat");
																BasicDBList mname = (BasicDBList) bobj.get("MName");
																BasicDBList mrebate = (BasicDBList) bobj.get("MRebate");
																BasicDBList age = (BasicDBList) bobj.get("Age");
																BasicDBList uname = (BasicDBList) bobj.get("UName");
																BasicDBList gender = (BasicDBList) bobj.get("Gender");
																BasicDBList occu = (BasicDBList) bobj.get("Occu");


																rowCount++;

																if (groupByShowHighestPrice==true) {

																								maxPrice=bobj.getInt("maxPrice");

																								tableData = "<tr><td><h3>Product: "+bobj.getString("Product")+"</h3></td>&nbsp"
																																				+ "<td>Max Product Price: "+bobj.getString("maxPrice")+"</td></tr>";

																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);

																								if (countOnly!=true) {

																																//Now print the products with the given review rating
																																while (reviewCount < productReview.size()) {


																																								int productPrice=(Integer)price.get(reviewCount);

																																								if (productPrice==maxPrice) {


																																																tableData = "<tr rowspan = \"3\"><td>Rating: "+rating.get(reviewCount)+"</br>"
																																																												+ "Review: "+productReview.get(reviewCount)+"</br>"
																																																												+ "Price: $"+price.get(reviewCount)+"</br>"
																																																												+ "Review Date: "+formatdate.format(rdate.get(reviewCount))+"</br>"
																																																												+ "City: "+rcity.get(reviewCount)+"</br>"
																																																												+ "Zip: "+zip.get(reviewCount)+"</br>"
																																																												+ "State: "+state.get(reviewCount)+"</br>"
																																																												+ "Retailer Name: "+rname.get(reviewCount)+"</br>"
																																																												+ "Sale: "+sale.get(reviewCount)+"</br>"
																																																												+ "Category: "+cat.get(reviewCount)+"</br>"
																																																												+ "M Name: "+mname.get(reviewCount)+"</br>"
																																																												+ "M Reabte: "+mrebate.get(reviewCount)+"</br>"
																																																												+ "Age: "+age.get(reviewCount)+"</br>"
																																																												+ "UserID: "+uname.get(reviewCount)+"</br>"
																																																												+ "Gender: "+gender.get(reviewCount)+"</br>"
																																																												+ "Occupation: "+occu.get(reviewCount)+"</td></tr>";

																																																pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																																																output.println(pageContent);
																																								}

																																								reviewCount++;
																																}
																								}
																} else {


																								tableData = "<tr><td><h3>Product: "+bobj.getString("Product")+"</h3></td>&nbsp"
																																				+ "<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																								output.println(pageContent);

																								if (countOnly!=true) {

																																//Now print the products with the given review rating
																																while (reviewCount < productReview.size()) {
																																								tableData = "<tr rowspan = \"3\"><td>Rating: "+rating.get(reviewCount)+"</br>"
																																																				+ "Review: "+productReview.get(reviewCount)+"</br>"
																																																				+ "Price: $"+price.get(reviewCount)+"</br>"
																																																				+ "Review Date: "+formatdate.format(rdate.get(reviewCount))+"</br>"
																																																				+ "City: "+rcity.get(reviewCount)+"</br>"
																																																				+ "Zip: "+zip.get(reviewCount)+"</br>"
																																																				+ "State: "+state.get(reviewCount)+"</br>"
																																																				+ "Retailer Name: "+rname.get(reviewCount)+"</br>"
																																																				+ "Sale: "+sale.get(reviewCount)+"</br>"
																																																				+ "Category: "+cat.get(reviewCount)+"</br>"
																																																				+ "M Name: "+mname.get(reviewCount)+"</br>"
																																																				+ "M Reabte: "+mrebate.get(reviewCount)+"</br>"
																																																				+ "Age: "+age.get(reviewCount)+"</br>"
																																																				+ "UserID: "+uname.get(reviewCount)+"</br>"
																																																				+ "Gender: "+gender.get(reviewCount)+"</br>"
																																																				+ "Occupation: "+occu.get(reviewCount)+"</td></tr>";

																																								pageContent = "<table class = \"query-table\">"+tableData+"</table>";
																																								output.println(pageContent);

																																								reviewCount++;

																																}
																								}

																}

																//Reset review count
																reviewCount = 0;

								}

								//No data found
								if(rowCount == 0) {
																pageContent = "<h3>No Data Found</h3>";
																output.println(pageContent);
								}
								//Reset value to false
								groupByShowHighestPrice=false;

}

/*public void constructGroupByReviewDateContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Review Date </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   Date reviewDateFetch= bobj.getDate("ReviewDate");
   DateFormat formatdate = new SimpleDateFormat("MM/dd/yyyy");
   String reviewDate = formatdate.format(reviewDateFetch);

   rowCount++;

   tableData = "<tr><td>Review Date: "+reviewDate+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    if (countOnly!=true){

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }

   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }

   public void constructGroupByCityContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int productCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - City </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   if (groupByShowHighestPrice==true){

    maxPrice=bobj.getInt("maxPrice");

    tableData = "<tr><td>City: "+bobj.getString("City")+"</td>&nbsp"
 + "<td>Max Product Price: "+bobj.getString("maxPrice")+"</td></tr>";

     pageContent = "<table class = \"query-table\">"+tableData+"</table>";
      output.println(pageContent);

     //Now print the products with the given review rating

    while (productCount < productList.size()) {

      int productPrice=(Integer)price.get(productCount);

      if (productPrice==maxPrice){
      tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(productCount)+"</br>"
 +   "Rating: "+rating.get(productCount)+"</br>"
 +	"Review: "+productReview.get(productCount)+"</br>"
 + "Price: $"+price.get(productCount)+"</td></tr>";

      pageContent = "<table class = \"query-table\">"+tableData+"</table>";
      output.println(pageContent);
      }
      productCount++;
     }

   } else {

    tableData = "<tr><td>City: "+bobj.getString("City")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (productCount < productList.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(productCount)+"</br>"
 +   "Rating: "+rating.get(productCount)+"</br>"
 +	"Review: "+productReview.get(productCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    productCount++;
   }

   }
   //Reset product count
   productCount =0;

   }
   //No data found
   if(rowCount == 0){
   pageContent = "<h1>No Data Found</h1>";
   output.println(pageContent);
   }
   //Reset value to false
   groupByShowHighestPrice=false;
   }

   public void constructGroupByZipCodeContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Zip Code </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   if (groupByShowHighestPrice==true){

    maxPrice=bobj.getInt("maxPrice");


    tableData = "<tr><td>Zipcode: "+bobj.getString("Zipcode")+"</td>&nbsp"
 + "<td>Max Product Price: "+bobj.getString("maxPrice")+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
     output.println(pageContent);

    //Now print the products with the given review rating
    while (reviewCount < productReview.size()) {
     int productPrice=(Integer)price.get(reviewCount);

     if (productPrice==maxPrice){
     tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

     pageContent = "<table class = \"query-table\">"+tableData+"</table>";
     output.println(pageContent);
    }

     reviewCount++;
    }

   } else{

   tableData = "<tr><td>Zipcode: "+bobj.getString("Zipcode")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
      output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }

   }

   //Reset review count
   reviewCount = 0;

   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   //Reset value to false
   groupByShowHighestPrice=false;
   }

   public void constructGroupByStateContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - State </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>State: "+bobj.getString("State")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
      output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }

   //Reset review count
   reviewCount = 0;

   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }

   public void constructGroupByProductCategoryContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Product Category </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Product Category: "+bobj.getString("Product Category")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
      output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }

   //Reset review count
   reviewCount = 0;

   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }

   public void constructGroupByProductPriceContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Product Price </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Product Price: "+bobj.getString("Product Price")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }

   //Reset review count
   reviewCount = 0;

   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   } //constructGroupByReviewRatingContent

   public void constructGroupByReviewRatingContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Review Rating </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Review Rating: "+bobj.getString("Review Rating")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   } //constructGroupByProductSaleContent

   public void constructGroupByProductSaleContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Product Sale </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Product On Sale: "+bobj.getString("Product Sale")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }

   public void constructGroupBymanuNameContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Manufacturer Name </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Manufacturer Name: "+bobj.getString("Manufacturer Name")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   } //constructGroupBymanuRebateContent

   public void constructGroupBymanuRebateContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - Manufacturer Rebate </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Manufacturer Rebate: "+bobj.getString("Manufacturer Rebate")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }

   public void constructGroupByUserIDContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - User ID </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>User ID: "+bobj.getString("UserID")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   } //constructGroupByUserGenderContent

   public void constructGroupByUserAgeContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - User Age </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>User Age: "+bobj.getString("UserAge")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }

   public void constructGroupByUserGenderContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - User Gender </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>User Gender: "+bobj.getString("UserGender")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   } //

   public void constructGroupByUserOccupationContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By - User Occupation </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>User Occupation: "+bobj.getString("UserOccupation")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }



   public void constructGroupByRetailerNameContent(AggregationOutput aggregate, PrintWriter output, boolean countOnly){
   int rowCount = 0;
   int reviewCount = 0;
   String tableData = " ";
   String pageContent = " ";
   int maxPrice=0;

   output.println("<h1> Grouped By -Retailer Name </h1>");
   for (DBObject result : aggregate.results()) {
   BasicDBObject bobj = (BasicDBObject) result;
   BasicDBList productList = (BasicDBList) bobj.get("Product");
   BasicDBList productReview = (BasicDBList) bobj.get("Reviews");
   BasicDBList rating = (BasicDBList) bobj.get("Rating");
   BasicDBList price = (BasicDBList) bobj.get("Price");

   rowCount++;

   tableData = "<tr><td>Retailer Name: "+bobj.getString("RetailerName")+"</td>&nbsp"
 +	"<td>Reviews Found: "+bobj.getString("Review Count")+"</td></tr>";

   pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

   //Now print the products with the given review rating
   while (reviewCount < productReview.size()) {
    tableData = "<tr rowspan = \"3\"><td> Product: "+productList.get(reviewCount)+"</br>"
 +   "Rating: "+rating.get(reviewCount)+"</br>"
 +	"Review: "+productReview.get(reviewCount)+"</br>"
 + "Price: $"+price.get(reviewCount)+"</td></tr>";

    pageContent = "<table class = \"query-table\">"+tableData+"</table>";
    output.println(pageContent);

    reviewCount++;
   }
   //Reset review count
   reviewCount = 0;
   }

   //No data found
   if(rowCount == 0){
   pageContent = "<h3>No Data Found</h3>";
   output.println(pageContent);
   }
   }*/


}
