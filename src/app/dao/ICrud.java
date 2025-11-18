package app.dao;
import java.util.List;

public interface ICrud<T> {
    void salvar(T obj);
    List<T> listarTodos();
    void deletar(int id);
    void atualizar(T obj);
}