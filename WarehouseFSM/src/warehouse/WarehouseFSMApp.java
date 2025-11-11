package warehouse;

public class WarehouseFSMApp {
  public static void main(String[] args) {
    WarehouseContext.instance().process();
  }
}
