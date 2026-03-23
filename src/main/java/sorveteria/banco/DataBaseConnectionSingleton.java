package sorveteria.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
padrão Singleton
 */
public class DataBaseConnectionSingleton {
    private static final String URL = "jdbc:postgresql://localhost:5432/sorveteria";
    private static String USUARIO = "postgres";
    private static String SENHA = "sorvete";

    private static DataBaseConnectionSingleton instace;
    private Connection conexao;

    private DataBaseConnectionSingleton(){
        try{
            this.conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Conexão estabelecida");
        }catch (SQLException e){
            throw new RuntimeException("Erro ao conectar ao banco" + e.getMessage(),e);
        }
    }

        public static DataBaseConnectionSingleton getInstance() {
            if (instace == null){
                instace = new DataBaseConnectionSingleton();
            }
            return instace;
        }

        public Connection getConection(){
            try{
                if (conexao == null || conexao.isClosed()){
                    conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                }
            }catch (SQLException e){
                throw new RuntimeException("Conexão falha:" + e.getMessage(), e);
            }
            return conexao;


        }

        public void fechar(){
            try{
                if (conexao != null && !conexao.isClosed()){
                    conexao.close();
                    System.out.println("Conexão fechada");
                }
            } catch (SQLException e){
                System.out.println("Erro ao tentar fechar" + e.getMessage());
            }
        }


}

