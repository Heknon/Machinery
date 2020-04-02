package me.oriharel.machinery.items;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Fuel {

  private final double amount;
  private final ItemStack associatedItem;

  public Fuel(double amount, ItemStack associatedItem) {
    this.amount = amount;
    this.associatedItem = associatedItem;
  }

  public double getAmount() {
    return amount;
  }

  public ItemStack getAssociatedItem() {
    return associatedItem;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Fuel fuel = (Fuel) o;
    return Double.compare(fuel.amount, amount) == 0 && associatedItem.equals(fuel.associatedItem);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, associatedItem);
  }

  @Override
  public String toString() {
    return "Fuel{" + "amount=" + amount + ", associatedItem=" + associatedItem + '}';
  }
}
