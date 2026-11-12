package repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    void salvar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> listar();
    void atualizar(T entidade);
    void excluir(ID id);
}
