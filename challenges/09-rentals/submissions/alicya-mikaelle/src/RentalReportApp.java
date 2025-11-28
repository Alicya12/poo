public class RentalReportApp {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Uso correto: java RentalReportApp <valorMinimo>");
            return;
        }

        double minimo = Double.parseDouble(args[0]);

        RentalSummary summary = new RentalSummary("db.properties");
        summary.gerarResumo(minimo);
    }
}


