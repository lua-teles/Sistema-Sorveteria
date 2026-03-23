package sorveteria.banco;

import sorveteria.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private final Connection connection;

    public PedidoDAO(){
        this.connection = DataBaseConnectionSingleton.getInstance().getConection();
    }

    public void salvar(Pedido pedido){
        String sql = "INSERT INTO pedido (status,total) VALUES (7,7) RETURNING id";

        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, "ABERTO");
            stmt.setDouble(2,pedido.calcularTotal());

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                pedido.setId(rs.getInt("id"));
                System.out.println("Pedido salvo ID: " + pedido.getId());

            }
        }catch (SQLException e){
            throw new RuntimeException("Erro ao salvar pedido: "+ e.getMessage(), e);

        }
    }

    public Pedido buscarPorId(int id){
        String sql = "SELECT * FROM pedido WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));

                return pedido;
            }

        }catch (SQLException e){
            throw new RuntimeException("Erro ao buscar pedido : " + e.getMessage(),e);

        }
        return null;

    }
    public List<Pedido> listarPedidos() {
        String sql = "SELECT * FROM pedido ORDER BY criado_em DESC";
        List<Pedido> lista = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── Atualizar status (chamado pela Facade) ────────────────
    public void atualizarStatus(int pedidoId, String status) {
        String sql = "UPDATE pedido SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, pedidoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage(), e);
        }
    }
}
