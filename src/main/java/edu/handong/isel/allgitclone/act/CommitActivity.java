package edu.handong.isel.allgitclone.act;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.handong.isel.allgitclone.control.GithubService;
import edu.handong.isel.allgitclone.control.RetroBasic;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CommitActivity {
	
	private static Retrofit retrofit = null;
	private boolean check_blank = false;
	private boolean check_over_limits = false;		//indicate which the page is over 10 or not.
	private String last_date = null;			//standard
	private ArrayList<String> commitResult = null;
	
	
	public CommitActivity() {
		commitResult = new ArrayList<>();
		RetroBasic new_object = new RetroBasic();
		new_object.createObject();
		retrofit = new_object.getObject();
	}
	
	
	public void start (BufferedWriter bw, PrintWriter pw, HashMap<String, String> commitOption) {
		GithubService service = retrofit.create(GithubService.class);
		Call<JsonObject> request = service.getUserCommits(commitOption);
		
		
		try {
			Response<JsonObject> response = request.execute();
			
			if (response.message().equals("Forbidden")) {
				System.out.println("Request is being processed. Please wait.\n");
				check_over_limits = true;
				return;
			}
			
			else
				check_over_limits = false;
			
			
			JsonArray json_com = new Gson().fromJson(response.body().get("items"), JsonArray.class);
			
			if (json_com.size() == 0) {
				System.out.println("There is no content ever.");
				check_blank = true;
				return;
			}
			
			for (int i = 0; i < json_com.size(); i++) {
				JsonObject item = new Gson().fromJson(json_com.get(i), JsonObject.class);
				JsonObject commits = new Gson().fromJson(item.get("commit"), JsonObject.class);
				JsonObject author = new Gson().fromJson(commits.get("author"), JsonObject.class);
				
				JsonObject repo = new Gson().fromJson(item.get("repository"), JsonObject.class);
				
				if(!commitResult.contains(repo.get("html_url").getAsString())) {
					System.out.println(repo.get("html_url"));
					commitResult.add(repo.get("html_url").getAsString());
				}
				
				if (i == json_com.size() - 1)
					last_date = author.get("date").getAsString();

			}
			
			System.out.println(response.body().get("total_count"));
			System.out.println("100 result successful");
			
		}
		
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
	public boolean isCheck_blank() {
		return check_blank;
	}

	public boolean isCheck_over_limits() {
		return check_over_limits;
	}

	public String getLast_date() {
		return last_date;
	}
	
}
