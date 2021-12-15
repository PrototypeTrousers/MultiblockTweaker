package eutros.multiblocktweaker.crafttweaker.gtwrap.constants;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.IBracketHandler;
import eutros.multiblocktweaker.crafttweaker.gtwrap.impl.MCMultiblockAbility;
import eutros.multiblocktweaker.crafttweaker.gtwrap.interfaces.IMultiblockAbility;
import eutros.multiblocktweaker.helper.ReflectionHelper;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMemberGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BracketHandler
@ZenClass("mods.gregtech.multiblock.MultiblockAbilities")
@ZenRegister
public class ConstantMultiblockAbilities implements IBracketHandler {
    private final static Map<String, IMultiblockAbility> cache = new HashMap<>();

    private final IJavaMethod method;

    public ConstantMultiblockAbilities() {
        this.method = CraftTweakerAPI.getJavaMethod(ConstantMultiblockAbilities.class, "get", String.class);

    }
    
    @ZenMethod
    @ZenMemberGetter()
    public static IMultiblockAbility get(String member) {
        if (!cache.containsKey(member)) {
            MultiblockAbility<?> ability = ReflectionHelper.getStatic(MultiblockAbility.class, member);
            cache.put(member, ability == null ? null : new MCMultiblockAbility<>(ability));
        }
        return cache.get(member);
    }


    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if ((tokens.size() < 3)) return null;
        if (!tokens.get(0).getValue().equalsIgnoreCase("mteAbility")) return null;
        if (!tokens.get(1).getValue().equals(":")) return null;
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 2; i < tokens.size(); i++) {
            nameBuilder.append(tokens.get(i).getValue());
        }
        return position -> new ExpressionCallStatic(position, environment, method,
                new ExpressionString(position, nameBuilder.toString()));
    }
}
