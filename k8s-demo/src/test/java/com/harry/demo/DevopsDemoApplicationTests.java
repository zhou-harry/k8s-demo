package com.harry.demo;

import com.harry.demo.controller.AsyncController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DevopsDemoApplicationTests {

	@Autowired
	private AsyncController controller;

	@Test
	public void contextLoads() {
	}

	@Test
	public void asyncCall() throws ExecutionException, InterruptedException {
		controller.asyncCall();
	}

}
