package Menu;

public class Menu {
    public static void menuPrincipal() {
        System.out.println("\n==== MENU PRINCIPAL ====");
        System.out.println("1 - Cadastro");
        System.out.println("2 - Busca");
        System.out.println("3 - Consultas");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    public static void menuCadastro() {
        System.out.println("\n=== CADASTRO ===");
        System.out.println("1 - Tutor");
        System.out.println("2 - Animal");
        System.out.println("3 - Veterinário");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuCadastroTutor() {
        System.out.println("\n=== CADASTRO TUTOR ===");
        System.out.println("1 - Cadastrar tutor");
        System.out.println("2 - Excluir tutor");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuCadastroAnimal() {
        System.out.println("\n=== CADASTRO ANIMAL ===");
        System.out.println("1 - Cadastrar Gato");
        System.out.println("2 - Cadastrar Cachorro");
        System.out.println("3 - Excluir Animal");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuCadastroVet() {
        System.out.println("\n=== CADASTRO VETERINÁRIO ===");
        System.out.println("1 - Cadastrar veterinário");
        System.out.println("2 - Excluir veterinário");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuBusca() {
        System.out.println("\n=== BUSCA ===");
        System.out.println("1 - Animal");
        System.out.println("2 - Tutor");
        System.out.println("3 - Veterinário");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuBuscaAnimal() {
        System.out.println("\n=== BUSCA ANIMAL ===");
        System.out.println("1 - Listar todos os gatos");
        System.out.println("2 - Listar todos os cachorros");
        System.out.println("3 - Buscar gato por nome");
        System.out.println("4 - Buscar cachorro por nome");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuBuscaTutor() {
        System.out.println("\n=== BUSCA TUTOR ===");
        System.out.println("1 - Listar todos os tutores");
        System.out.println("2 - Buscar tutor por nome");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuBuscaVet() {
        System.out.println("\n=== BUSCA VETERINÁRIO ===");
        System.out.println("1 - Listar todos os veterinários");
        System.out.println("2 - Buscar veterinário por nome");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }

    public static void menuConsultas() {
        System.out.println("\n=== CONSULTAS ===");
        System.out.println("1 - Agendar consulta");
        System.out.println("2 - Listar todas as consultas");
        System.out.println("3 - Listar consultas por animal");
        System.out.println("4 - Preencher receita e gerar TXT");
        System.out.println("0 - Voltar");
        System.out.print("Escolha: ");
    }
}
