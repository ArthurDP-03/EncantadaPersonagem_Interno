import { useAdministradores } from "../../hooks/useAdministradores";
import { useAtores } from "../../hooks/useAtores";
import Card_linha from "../card_linha";
import './index.css'
import { Search, ChevronDown, Plus } from "lucide-react";
import Swal from "sweetalert2";
import { useState } from "react";

const adminVazio = { nome: "", email: "", telefone: "", senha: "", tipo: "ADMIN" };
const atorVazio  = { nome: "", email: "", senha: "", telefone: "", genero: "", altura: "", peso: "", observacao: "" };

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
  const { administradores, carregando: carregandoAdministradores, erro: erroAdministradores, deletar: deletarAdmin, criar: criarAdmin, editar: editarAdmin } = useAdministradores();
  const { atores, carregando: carregandoAtores, erro: erroAtores, deletar: deletarAtor, criar: criarAtor, editar: editarAtor } = useAtores();

  const [modalCriar, setModalCriar]   = useState(false);
  const [modalEditar, setModalEditar] = useState(null); 
  const [tipoForm, setTipoForm]       = useState("admin");
  const [formAdmin, setFormAdmin]     = useState(adminVazio);
  const [formAtor, setFormAtor]       = useState(atorVazio);

  if (carregandoAdministradores || carregandoAtores) return <p>Carregando...</p>;
  if (erroAdministradores || erroAtores) return <p>Erro: {erroAdministradores || erroAtores}</p>;

  function handleCriar(event) {
    event.preventDefault();
    if (tipoForm === "admin") {
      if (!formAdmin.nome.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Nome é obrigatório para preenchimento" });
        return;
      }
      if (!formAdmin.email.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Email é obrigatório para preenchimento" });
        return;
      }
      if (!formAdmin.telefone.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Telefone é obrigatório para preenchimento" });
        return;
      }
      if (!formAdmin.senha.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Senha é obrigatório para preenchimento" });
        return;
      }
      if (!formAdmin.tipo?.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Tipo é obrigatório para preenchimento" });
        return;
      }

      criarAdmin(formAdmin)
        .then(() => {
          setModalCriar(false);
          setFormAdmin(adminVazio);
        })
        .catch(err => {
          console.error("Erro ao criar administrador:", err);
          // O erro é tratado no hook com Swal
        });
    } else {
      // Ator
      if (!formAtor.nome.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Nome é obrigatório para preenchimento" });
        return;
      }
      if (!formAtor.email.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Email é obrigatório para preenchimento" });
        return;
      }
      if (!formAtor.telefone.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Telefone é obrigatório para preenchimento" });
        return;
      }
      if (!formAtor.senha.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Senha é obrigatório para preenchimento" });
        return;
      }
      if (!formAtor.genero.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Gênero é obrigatório para preenchimento" });
        return;
      }
      if (formAtor.altura === "" || formAtor.altura === null || formAtor.altura === undefined || isNaN(Number(formAtor.altura))) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Altura é obrigatório para preenchimento e deve ser um número" });
        return;
      }
      if (formAtor.peso === "" || formAtor.peso === null || formAtor.peso === undefined || isNaN(Number(formAtor.peso))) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Peso é obrigatório para preenchimento e deve ser um número" });
        return;
      }

      criarAtor({
        ...formAtor,
        altura: parseFloat(formAtor.altura),
        peso: parseFloat(formAtor.peso),
        ativo: true
      })
        .then(() => {
          setModalCriar(false);
          setFormAtor(atorVazio);
        })
        .catch(err => {
          console.error("Erro ao criar ator:", err);
          // O erro é tratado no hook com Swal
        });
    }
  }

  function handleEditar(event) {
    event.preventDefault();
    if (!modalEditar) return;

    // validações para edição (admin ou ator)
    const dados = modalEditar.dados || {};
    if (!dados.nome?.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Nome é obrigatório para preenchimento" });
      return;
    }
    if (!dados.email?.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Email é obrigatório para preenchimento" });
      return;
    }
    if (!dados.telefone?.trim()) {
      Swal.fire({ icon: "warning", title: "Validação", text: "O campo Telefone é obrigatório para preenchimento" });
      return;
    }

    if (modalEditar.tipo === "admin") {
      if (!dados.senha?.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Senha é obrigatório para preenchimento" });
        return;
      }
      if (!dados.tipo?.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Tipo é obrigatório para preenchimento" });
        return;
      }

      editarAdmin(modalEditar.dados.id, modalEditar.dados)
        .then(() => {
          setModalEditar(null);
        })
        .catch(err => {
          console.error("Erro ao editar administrador:", err);
          // O erro é tratado no hook com Swal
        });
    } else {
      // ator
      if (!dados.senha?.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Senha é obrigatório para preenchimento" });
        return;
      }
      if (!dados.genero?.trim()) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Gênero é obrigatório para preenchimento" });
        return;
      }
      if (dados.altura === "" || dados.altura === null || dados.altura === undefined || isNaN(Number(dados.altura))) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Altura é obrigatório para preenchimento e deve ser um número" });
        return;
      }
      if (dados.peso === "" || dados.peso === null || dados.peso === undefined || isNaN(Number(dados.peso))) {
        Swal.fire({ icon: "warning", title: "Validação", text: "O campo Peso é obrigatório para preenchimento e deve ser um número" });
        return;
      }

      editarAtor(modalEditar.dados.id, {
        ...modalEditar.dados,
        altura: parseFloat(modalEditar.dados.altura),
        peso: parseFloat(modalEditar.dados.peso),
      })
        .then(() => {
          setModalEditar(null);
        })
        .catch(err => {
          console.error("Erro ao editar ator:", err);
          // O erro é tratado no hook com Swal
        });
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
                  informacoes={{ Email: admin.email, Celular: admin.telefone }}
                  onEditar={() => setModalEditar({ tipo: "admin", dados: { ...admin, senha: "" } })}
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
                  informacoes={{ Email: ator.email, Celular: ator.telefone, Gênero: ator.genero, Altura: ator.altura, Peso: ator.peso, Observação: ator.observacao }}
                  onEditar={() => setModalEditar({ tipo: "ator", dados: { ...ator, senha: "" } })}
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
              <button type="button" className={tipoForm === "ator"  ? "aba-ativa" : ""} onClick={() => setTipoForm("ator")}>Ator</button>
            </div>
            {tipoForm === "admin"
              ? <FormAdministrador dados={formAdmin} onChange={setFormAdmin} />
              : <FormAtor dados={formAtor} onChange={setFormAtor} />
            }
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => { setModalCriar(false); setFormAdmin(adminVazio); setFormAtor(atorVazio); }}>Cancelar</button>
              <button type="submit" className="btn-primario">Criar</button>
            </div>
          </form>
        </div>
      )}

      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">
              Editar {modalEditar.tipo === "admin" ? "Administrador" : "Ator"}
            </h2>
            {modalEditar.tipo === "admin"
              ? <FormAdministrador dados={modalEditar.dados} onChange={dados => setModalEditar({ ...modalEditar, dados })} />
              : <FormAtor dados={modalEditar.dados} onChange={dados => setModalEditar({ ...modalEditar, dados })} />
            }
            <div className="modal-acoes">
              <button type="button" className="btn-secundario" onClick={() => setModalEditar(null)}>Cancelar</button>
              <button type="submit" className="btn-primario">Salvar</button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default Colaboradores;