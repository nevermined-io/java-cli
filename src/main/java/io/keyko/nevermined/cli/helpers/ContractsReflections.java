package io.keyko.nevermined.cli.helpers;

import io.keyko.nevermined.contracts.*;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class ContractsReflections {

    public static final String CONTRACTS_PACKAGE = "io.keyko.nevermined.contracts";
    private static final String WEB3J_METHOD_PREFIX= "FUNC_";
    private static final String WEB3J_EVENT_SUFFIX= "_EVENT";


    public static final HashMap<String, Class> CONTRACT_CLASSES = new HashMap<>() {{
        put("AccessCondition", AccessCondition.class);
        put("AccessTemplate", AccessTemplate.class);
        put("AgreementStoreManager", AgreementStoreManager.class);
        put("ComputeExecutionCondition", ComputeExecutionCondition.class);
        put("ConditionStoreManager", ConditionStoreManager.class);
        put("DIDRegistry", DIDRegistry.class);
        put("DIDSalesTemplate", DIDSalesTemplate.class);
        put("Dispenser", Dispenser.class);
        put("EscrowComputeExecutionTemplate", EscrowComputeExecutionTemplate.class);
        put("EscrowPaymentCondition", EscrowPaymentCondition.class);
        put("HashLockCondition", HashLockCondition.class);
        put("NeverminedToken", NeverminedToken.class);
        put("NFTAccessCondition", NFTAccessCondition.class);
        put("NFTAccessTemplate", NFTAccessTemplate.class);
        put("NFTHolderCondition", NFTHolderCondition.class);
        put("NFTLockCondition", NFTLockCondition.class);
        put("NFTSalesTemplate", NFTSalesTemplate.class);
        put("LockPaymentCondition", LockPaymentCondition.class);
        put("SignCondition", SignCondition.class);
        put("TemplateStoreManager", TemplateStoreManager.class);
        put("ThresholdCondition", ThresholdCondition.class);
        put("TransferDIDOwnershipCondition", TransferDIDOwnershipCondition.class);
        put("TransferNFTCondition", TransferNFTCondition.class);
        put("WhitelistingCondition", WhitelistingCondition.class);
    }};

    public static boolean contractExists(String contractName)   {
        return CONTRACT_CLASSES.containsKey(contractName);
    }

    public static Class getContractClass(String contractName)   {
        return CONTRACT_CLASSES.get(contractName);
    }

    public static Collection<Class> getConstructors()    {
        return CONTRACT_CLASSES.values();
    }

    public static void printClassInformation(Class clazz, PrintWriter out)  {
        out.println("Contract: " + clazz.getSimpleName());
        printConstructorsInformation(clazz.getConstructors(), out);

        out.println("Methods: ");
        getWeb3jPublicMethods(clazz.getDeclaredFields()).forEach(
                name -> out.println("\t" + name)
        );

        out.println("Events: ");
        getWeb3jEvents(clazz.getDeclaredFields()).forEach(
                name -> out.println("\t" + name)
        );
    }

    public static List<String> getWeb3jPublicMethods(Field[] fields)    {
        List<String> methods= new ArrayList<>();
        for (Field f: fields)   {
            if (f.getName().startsWith(WEB3J_METHOD_PREFIX))
                methods.add(f.getName()
                        .substring(WEB3J_METHOD_PREFIX.length())
                        .toLowerCase());
        }
        return methods;
    }


    public static List<String> getWeb3jEvents(Field[] fields)    {
        List<String> methods= new ArrayList<>();
        for (Field f: fields)   {
            if (f.getName().endsWith(WEB3J_EVENT_SUFFIX))
                methods.add(f.getName()
                        .substring(0, f.getName().length() - WEB3J_EVENT_SUFFIX.length())
                        .toLowerCase()
                );
        }
        return methods;
    }

    public static void printConstructorsInformation(Constructor<?>[] constructors, PrintWriter out)  {
        for (Constructor c: constructors)   {
            out.println("\tConstructor:" + c.getName() +"(");
            for (Parameter p: c.getParameters())    {
                out.print(" " + p.getType().getName() + " " + p.getName() + ",");
            }
            out.println(")");
        }
    }

}
