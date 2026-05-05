import { useAdministradores } from "../../hooks/useAdminstradores";
import { useAtores } from "../../hooks/useAtores";
import Card_linha from "../card_linha";
import './index.css'
import { Search, ChevronDown, Plus } from "lucide-react";
import { useState } from "react";

const adminVazio = { nome: "", email: "", telefone: "", senha: "", tipo: "ADMIN" };
const atorVazio = { nome: "", email: "",  senha: "", telefone: "", genero: "", altura: "", peso: "", observacao: "", ativo: "" };

function FormAdministrador({ dados, onChange }) {
  return (
    <div className="form-colaborador">
      <label className="campo-largo">Nome
        <input value={dados.nome} onChange={e => onChange({ ...dados, nome: e.target.value })} placeholder="Nome completo" />
      </label>
      <label className="campo-largo">Email
        <input value={dados.email} onChange={e => onChange({ ...dados, email: e.target.value })} placeholder="email@exemplo.com" />
      </label>
      <label>Telefone
        <input value={dados.telefone} onChange={e => onChange({ ...dados, telefone: e.target.value })} placeholder="41 9 9999-0000" />
      </label>
      <label>Senha
        <input type="password" value={dados.senha} onChange={e => onChange({ ...dados, senha: e.target.value })} placeholder="Senha" />
      </label>
    </div>
  );
}

function FormAtor({ dados, onChange }) {
  return (
    <div className="form-colaborador">
      <label className="campo-largo">Nome
        <input value={dados.nome} onChange={e => onChange({ ...dados, nome: e.target.value })} placeholder="Nome completo" />
      </label>
      <label className="campo-largo">Email
        <input value={dados.email} onChange={e => onChange({ ...dados, email: e.target.value })} placeholder="email@exemplo.com" />
      </label>
      <label>Senha
        <input type="password" value={dados.senha} onChange={e => onChange({ ...dados, senha: e.target.value })} placeholder="Senha" />
      </label>
      <label>Telefone
        <input value={dados.telefone} onChange={e => onChange({ ...dados, telefone: e.target.value })} placeholder="41 9 9999-0000" />
      </label>
      <label>Gênero
        <select value={dados.genero} onChange={e => onChange({ ...dados, genero: e.target.value })}>
          <option value="">Selecione</option>
          <option value="Masculino">Masculino</option>
          <option value="Feminino">Feminino</option>
          <option value="Outro">Outro</option>
        </select>
      </label>
      <label>Altura (m)
        <input type="number" step="0.01" value={dados.altura} onChange={e => onChange({ ...dados, altura: e.target.value })} placeholder="1.80" />
      </label>
      <label>Peso (kg)
        <input type="number" value={dados.peso} onChange={e => onChange({ ...dados, peso: e.target.value })} placeholder="78" />
      </label>
      <label className="campo-largo">Observação
        <textarea value={dados.observacao} onChange={e => onChange({ ...dados, observacao: e.target.value })} placeholder="Observações..." />
      </label>
    </div>
  );
}

function Colaboradores() {
  const { administradores, carregando: carregandoAdministradores, erro: erroAdministradores, deletar: deletarAdmin, criar: criarAdmin } = useAdministradores();
  const { atores, carregando: carregandoAtores, erro: erroAtores, deletar: deletarAtor, criar: criarAtor } = useAtores();
  const [modalCriar, setModalCriar] = useState(false);
  const [tipoForm, setTipoForm] = useState("admin");
  const [formAdmin, setFormAdmin] = useState(adminVazio);
  const [formAtor, setFormAtor] = useState(atorVazio);

  if (carregandoAdministradores || carregandoAtores) return <p>Carregando...</p>;
  if (erroAdministradores || erroAtores) return <p>Erro: {erroAdministradores || erroAtores}</p>;

  function handleCriar(event) {
    event.preventDefault();
    if (tipoForm === "admin") {
      criarAdmin(formAdmin).then(() => { setModalCriar(false); setFormAdmin(adminVazio); });
    } else {
      const atorFormatado = {
        ...formAtor,
        altura: parseFloat(formAtor.altura),
        peso: parseFloat(formAtor.peso),
        ativo: true
      };
      console.log(atorFormatado)
      criarAtor(atorFormatado).then(() => { setModalCriar(false); setFormAtor(atorVazio); });
    }
  }

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
      </div>

      <button className="btn-create" onClick={() => setModalCriar(true)}>
        <Plus size={24} />
      </button>

      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">Novo Colaborador</h2>

            <div className="modal-abas">
              <button type="button" className={tipoForm === "admin" ? "aba-ativa" : ""} onClick={() => setTipoForm("admin")}>Administrador</button>
              <button type="button" className={tipoForm === "ator" ? "aba-ativa" : ""} onClick={() => setTipoForm("ator")}>Ator</button>
            </div>

            {tipoForm === "admin"
              ? <FormAdministrador dados={formAdmin} onChange={setFormAdmin} />
              : <FormAtor dados={formAtor} onChange={setFormAtor} />
            }

            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => setModalCriar(false)}>Cancelar</button>
              <button type="submit" className="btn-primario">Criar</button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default Colaboradores; 