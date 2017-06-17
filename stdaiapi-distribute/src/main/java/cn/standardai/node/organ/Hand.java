package cn.standardai.node.organ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.standardai.node.exec.Client;
import cn.standardai.node.exec.Post;

public class Hand {

	public void knock() {

		System.out.println("Please enter the server port:");
		String port = null;
		try {
			port = new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Post server = new Post(Integer.parseInt(port));
		Client client = new Client();
	}
}
