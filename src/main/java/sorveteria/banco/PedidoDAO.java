package sorveteria.banco;

import sorveteria.model.Pedido;
import sorveteria.state.PedidoAbertoState;
import sorveteria.state.PedidoFinalizadoState;
import sorveteria.state.PedidoPreparoState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    private final Connection connection;

    public PedidoDAO(){
        this.connection = DataBaseConnectionSingleton.getInstance().getConection();
    }

    // insere um pedido novo no banco com status ABERTO.
    // usa apenas UM INSERT com todos os campos (status, total, descricao).
    public void salvar(Pedido pedido) {
        String sql = "INSERT INTO pedido (status, total, descricao) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "ABERTO");
            stmt.setDouble(2, pedido.calcularTotal());
            stmt.setString(3, pedido.getDescricaoResumida());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pedido.setId(rs.getInt("id"));
                System.out.println("Pedido salvo ID: " + pedido.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage(), e);
        }
    }

    public Pedido buscarPorId(int id) {
        String sql = "SELECT * FROM pedido WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setDescricaoPersistida(rs.getString("descricao"));
                pedido.setTotalPersistido(rs.getDouble("total"));
                String status = rs.getString("status");
                switch (status) {
                    case "ABERTO"     -> pedido.setEstado(new PedidoAbertoState());
                    case "PREPARO"    -> pedido.setEstado(new PedidoPreparoState());
                    case "FINALIZADO" -> pedido.setEstado(new PedidoFinalizadoState());
                }
                return pedido;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage(), e);
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
                p.setDescricaoPersistida(rs.getString("descricao"));
                p.setTotalPersistido(rs.getDouble("total"));
                String status = rs.getString("status");
                switch (status) {
                    case "ABERTO"     -> p.setEstado(new PedidoAbertoState());
                    case "PREPARO"    -> p.setEstado(new PedidoPreparoState());
                    case "FINALIZADO" -> p.setEstado(new PedidoFinalizadoState());
                }
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    // atualizar status (chamado pela Facade)
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

    public void atualizarDescricao(Pedido pedido) {
        String sql = "UPDATE pedido SET descricao = ?, total = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pedido.getDescricaoResumida());
            stmt.setDouble(2, pedido.calcularTotal());
            stmt.setInt(3, pedido.getId());
            stmt.executeUpdate();
            System.out.println("Pedido atualizado ID: " + pedido.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM pedido WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pedido: " + e.getMessage(), e);
        }
    }
}
