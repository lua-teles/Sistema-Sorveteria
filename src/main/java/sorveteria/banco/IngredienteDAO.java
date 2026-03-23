package sorveteria.banco;

import sorveteria.model.Ingrediente;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class IngredienteDAO {
    private final Connection connection;

    public IngredienteDAO(){
        this.connection = DataBaseConnectionSingleton.getInstance().getConection();
    }

    public Ingrediente buscaPorNome(String nome){
        String sql = "SELECT * FROM ingrediente WHERE nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new Ingrediente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("quantidade")
                );
            }
        }catch (SQLException e){
            throw new RuntimeException("Erro ao buscar ingrediente: "+e.getMessage(),e);
        }
        return null;

    }
    public void atualizarQuantidade(Ingrediente ingrediente){
        String sql = "UPDATE ingrediente SET quantidade = ? WHERE id = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1,ingrediente.getQuantidade());
            stmt.setInt(2,ingrediente.getId());
            stmt.executeUpdate();
            System.out.println("Estoque atualizado:" + ingrediente.getNome() + ingrediente.getQuantidade()+ "unidades");

        }catch (SQLException e){
            throw new RuntimeException("Erro ao atualizar ingrediente" +e.getMessage(), e);
        }
    }

    public List<Ingrediente> listarTodos() {
        String sql = "SELECT * FROM ingrediente ORDER BY nome";
        List<Ingrediente> lista = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Ingrediente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("quantidade")
                ));
            }
        }catch (SQLException e){
            throw new RuntimeException("Erro ao listar ingredientes"+ e.getMessage(),e);
        }
        return lista;
    }
}
