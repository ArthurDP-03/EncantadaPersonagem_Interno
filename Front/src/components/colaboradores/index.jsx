import { useAdministradores } from "../../hooks/useAdminstradores";
import { useAtores } from "../../hooks/useAtores";
import Card_linha from "../card_linha";
import './index.css'
import { Search, ChevronDown } from "lucide-react";

function Colaboradores() {
  const { administradores, carregandoAdministradores, erroAdministradores,  deletar: deletarAdmin } = useAdministradores();
  const { atores, carregandoAtores, erroAtores } = useAtores();

  if (carregandoAdministradores || carregandoAtores) return <p>Carregando...</p>;
  if (erroAdministradores || erroAtores) return <p>Erro: {erroAdministradores || erroAtores}</p>;

  return (
    <section className="section-colaboradores">
      <div className="conteudo-95 layout">
        <div className="conteudo">
          <h1 className="titulo t1">Colaboradores</h1>
          <div className="filtros">
            <div className="input-container">
              <select className="input">
                <option value="">Ordenar</option>
                <option value="">Alfabética(A-Z)</option>
                <option value="">Alfabética(Z-A)</option>
                <option value="">Disponibilidade</option>
              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input type="text" placeholder="Buscar" className="input" />
              <Search className="icon" size={18} />
            </div>
          </div>

          <div className="lista lista-administradores">
            <div className="titulo t2"><p>Administradores</p></div>
            <div className="cards">
              {administradores.map((admin) => (
                <Card_linha
                  key={admin.id}
                  titulo={admin.nome}
                  informacoes={{
                    Tipo: admin.tipo,
                    Email: admin.email,
                    Celular: admin.telefone,
                  }}
                  onDeletar={() => deletarAdmin(admin.id)}
                />
              ))}
            </div>
          </div>

          <div className="lista lista-atores">
            <div className="titulo t2"><p>Atores</p></div>
            <div className="cards">
              {atores.map((ator) => (
                <Card_linha
                  key={ator.id}
                  titulo={ator.nome}
                  informacoes={{
                    Email: ator.email,
                    Celular: ator.telefone,
                    Gênero: ator.genero,
                    Altura: ator.altura,
                    Peso: ator.peso,
                    Observação: ator.observacao,
                  }}
                />
              ))}
            </div>
          </div>

        </div>
      </div>
    </section>
  );
}

export default Colaboradores;