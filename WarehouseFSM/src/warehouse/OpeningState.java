package warehouse;

public class OpeningState implements WarehouseState {
  private final WarehouseContext ctx;

  public OpeningState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "OpeningState"; }

  @Override
  public void run() {
    while (ctx.getCurrentStateIndex() == WarehouseContext.OPENING_STATE) {
      System.out.println();
      System.out.println("== Login ==");
      System.out.println("1) Login as Client");
      System.out.println("2) Login as Clerk");
      System.out.println("3) Login as Manager");
      System.out.println("0) Quit");

      int choice = ctx.promptInt("> ");
      switch (choice) {
        case 0: {
          int ns = ctx.getNextState(WarehouseContext.OPENING_STATE, WarehouseContext.CMD_QUIT_OR_LOGOUT);
          ctx.setState(ns);
          break;
        }
        case 1: {
          String clientId = ctx.promptLine("Enter Client ID (e.g., C1): ");
          if (isValidClient(clientId)) {
            ctx.setCurrentClient(clientId);
            ctx.setPreviousState(WarehouseContext.OPENING_STATE);
            int ns = ctx.getNextState(WarehouseContext.OPENING_STATE, WarehouseContext.CMD_CLIENT);
            ctx.setState(ns);
          } else {
            System.out.println("Invalid client ID.");
          }
          break;
        }
        case 2: {
          int ns = ctx.getNextState(WarehouseContext.OPENING_STATE, WarehouseContext.CMD_CLERK);
          ctx.setState(ns);
          break;
        }
        case 3: {
          int ns = ctx.getNextState(WarehouseContext.OPENING_STATE, WarehouseContext.CMD_MANAGER);
          ctx.setState(ns);
          break;
        }
        default:
          System.out.println("Invalid option.");
      }
    }
  }

  private boolean isValidClient(String clientId) {
    for (Client c : ctx.warehouse().getAllClients()) {
      if (c.getId().equalsIgnoreCase(clientId)) return true;
    }
    return false;
  }
}
