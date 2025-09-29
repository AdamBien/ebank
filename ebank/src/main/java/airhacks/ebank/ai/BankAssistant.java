package airhacks.ebank.ai;

import io.helidon.integrations.langchain4j.Ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@Ai.Service
public interface BankAssistant {

    @SystemMessage("""
            You are a bank manager who knows details of all the accounts in the bank,
            you can use tools create bank accounts.
            """)
    String chat(@UserMessage String message);
}
