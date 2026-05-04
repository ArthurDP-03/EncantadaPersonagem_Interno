import { useAdministradores } from "../../hooks/useAdminstradores";
import { useAtores } from "../../hooks/useAtores";
import Card_linha from "../card_linha";
import './index.css'
import { Search, ChevronDown, Plus } from "lucide-react";
import { useState } from "react";

function Colaboradores() {
  const { administradores, carregando: carregandoAdministradores, erro: erroAdministradores, deletar: deletarAdmin, criar: criarAdmin } = useAdministradores();
  const { atores, carregando: carregandoAtores, erro: erroAtores, deletar: deletarAtor } = useAtores();
  const [modalAberto, setModalAberto] = useState(false);

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
                  onDeletar={() => deletarAtor(ator.id)}
                />
              ))}
            </div>
          </div>
        </div>
        <button className="btn-create" onClick={() => setModalAberto(true)}>
          <Plus size={24} />
        </button>
        {
          modalAberto && (
            <div className="modal">
              <form
                onSubmit={async (e) => {
                  e.preventDefault();

                  const formData = new FormData(e.target);

                  const novoAdmin = {
                    nome: formData.get("nome"),
                    email: formData.get("email"),
                    senha: formData.get("senha"),
                    telefone: formData.get("telefone"),
                    tipo: formData.get("tipo"),
                  };

                  await criarAdmin(novoAdmin);

                  setModalAberto(false);
                }}
              >
                <input name="nome" placeholder="Nome" />
                <input name="email" placeholder="Email" />
                <input name="senha" type="password" placeholder="Senha" />
                <input name="telefone" placeholder="Telefone" />
                <input name="tipo" placeholder="Tipo" />

                <button type="submit">
                  Salvar
                </button>
              </form>
            </div>
          )
        }
      </div>

    </section>
  );
}

export default Colaboradores;