package serviceLibrary.dto.tradeService;

public class TradeResponseDto {

    private String message;
    private Object accountState;

    public TradeResponseDto() {
    }

    public TradeResponseDto(String message, Object accountState) {
        this.message = message;
        this.accountState = accountState;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getAccountState() { return accountState; }
    public void setAccountState(Object accountState) { this.accountState = accountState; }
}