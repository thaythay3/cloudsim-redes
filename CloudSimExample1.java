</> Java

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class CloudSimExample1 {

    public static void main(String[] args) {

        try {
            // PASSO 1: Inicializar CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;

            CloudSim.init(numUsers, calendar, traceFlag);

            // PASSO 2: Criar Datacenter
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            // PASSO 3: Criar Broker
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            // PASSO 4: Criar VM
            List<Vm> vmList = new ArrayList<>();

            int vmid = 0;
            int mips = 1000;
            long size = 10000;
            int ram = 512;
            long bw = 1000;
            int pesNumber = 1;
            String vmm = "Xen";

            Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm,
                    new CloudletSchedulerTimeShared());

            vmList.add(vm);
            broker.submitVmList(vmList);

            // PASSO 5: Criar Cloudlet
            List<Cloudlet> cloudletList = new ArrayList<>();

            int id = 0;
            long length = 400000;
            long fileSize = 300;
            long outputSize = 300;

            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel);

            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(vmid);

            cloudletList.add(cloudlet);
            broker.submitCloudletList(cloudletList);

            // PASSO 6: Executar simulação
            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // PASSO 7: Mostrar resultados
            printCloudletList(newList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {

        List<Host> hostList = new ArrayList<>();

        List<Pe> peList = new ArrayList<>();
        int mips = 1000;

        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        int hostId = 0;
        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;

        hostList.add(new Host(
                hostId,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(bw),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
        ));

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";

        double timeZone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, timeZone, cost, costPerMem, costPerStorage, costPerBw);

        Datacenter datacenter = null;

        try {
            datacenter = new Datacenter(
                    name,
                    characteristics,
                    new VmAllocationPolicySimple(hostList),
                    new LinkedList<>(),
                    0
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    private static DatacenterBroker createBroker() {

        DatacenterBroker broker = null;

        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {

        for (Cloudlet cloudlet : list) {
            System.out.println("Cloudlet ID: " + cloudlet.getCloudletId()
                    + " | Status: " + cloudlet.getStatus()
                    + " | Tempo de execução: " + cloudlet.getActualCPUTime());
        }
    }
}

