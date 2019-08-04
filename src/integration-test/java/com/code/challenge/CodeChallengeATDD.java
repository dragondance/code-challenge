package com.code.challenge;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/integration-test/resources/features/statusTransaction.feature",
		plugin = {"pretty"}
)
public class CodeChallengeATDD {

}
