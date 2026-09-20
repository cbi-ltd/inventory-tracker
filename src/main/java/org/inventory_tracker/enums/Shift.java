package org.inventory_tracker.enums;

public enum Shift {
   MORNING(1),
   AFTERNOON(2),
   NIGHT(3),
   OVERNIGHT(4);

   private final int order;

   Shift(int order) {
        this.order = order;
   }

   public int getOrder() {
        return order;
   }
}
