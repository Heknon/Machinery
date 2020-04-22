package me.oriharel.machinery;

import com.google.gson.Gson;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.machine.MachineType;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<Fuel> fuels = Arrays.asList(new Fuel(1), new Fuel(5), new Fuel(7));
        List<Fuel> fuelsWithEnoughEnergy =
                fuels.stream().filter(fuel -> fuel.energy >= 4).collect(Collectors.toList());
        fuelsWithEnoughEnergy.get(0).energy = 1;
        System.out.println(fuelsWithEnoughEnergy.hashCode());
        System.out.println(fuels.hashCode());
    }


    public static class Fuel {
        int energy;
        public Fuel(int energy) {
            this.energy = energy;
        }

    }
}
