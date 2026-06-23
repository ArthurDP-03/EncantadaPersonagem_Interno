import "./index.css";
import { Search, ChevronDown, Plus } from "lucide-react";
import { useState } from "react";
import Swal from "sweetalert2";
import { useTranslation } from "react-i18next";
import { useClientes } from "../../hooks/useClientes";
import Card_linha from "../card_linha";

const clienteVazio = { nome: "", email: "", telefone: "" };
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function FormCliente({ dados, onChange }) {
  const { t } = useTranslation();

  return (
    <div className="form-cliente">
      <label>
        {t("common.fields.name")}
        <input
          value={dados.nome}
          onChange={(e) => onChange({ ...dados, nome: e.target.value })}
          placeholder={t("clients.placeholders.name")}
        />
      </label>
      <label>
        {t("common.fields.email")}
        <input
          value={dados.email}
          onChange={(e) => onChange({ ...dados, email: e.target.value })}
          placeholder={t("clients.placeholders.email")}
        />
      </label>
      <label>
        {t("common.fields.phone")}
        <input
          value={dados.telefone}
          onChange={(e) => onChange({ ...dados, telefone: e.target.value })}
          placeholder={t("clients.placeholders.phone")}
        />
      </label>
    </div>
  );
}

function Clientes() {
  const { t } = useTranslation();
  const [busca, setBusca] = useState("");
  const [ordem, setOrdem] = useState("");
  const [modalCriar, setModalCriar] = useState(false);
  const [modalEditar, setModalEditar] = useState(null);
  const [form, setForm] = useState(clienteVazio);
  const [salvando, setSalvando] = useState(false);

  const {
    clientes,
    carregando,
    erro,
    criar: adicionarCliente,
    editar: editarCliente,
    deletar: removerCliente,
  } = useClientes();

  if (carregando) return <p>{t("common.loading")}</p>;
  if (erro) return <p>{t("common.error", { message: erro })}</p>;

  const clientesFiltrados = clientes
    .filter(
      (c) =>
        c.nome.toLowerCase().includes(busca.toLowerCase()) ||
        c.email.toLowerCase().includes(busca.toLowerCase()),
    )
    .sort((a, b) => {
      if (ordem === "az") return a.nome.localeCompare(b.nome);
      if (ordem === "za") return b.nome.localeCompare(a.nome);
      return 0;
    });

  function mostrarValidacao(fieldKey) {
    Swal.fire({
      icon: "warning",
      title: t("common.validation.title"),
      text: t("common.validation.required", { field: t(fieldKey) }),
    });
  }

  function mostrarEmailInvalido() {
    Swal.fire({
      icon: "warning",
      title: t("common.validation.title"),
      text: t("common.validation.invalidEmail"),
    });
  }

  async function handleCriar(event) {
    event.preventDefault();
    if (salvando) return;

    if (!form.nome.trim()) {
      mostrarValidacao("common.fields.name");
      return;
    }
    if (!form.email.trim()) {
      mostrarValidacao("common.fields.email");
      return;
    }
    if (!form.telefone.trim()) {
      mostrarValidacao("common.fields.phone");
      return;
    }
    if (!emailRegex.test(form.email)) {
      mostrarEmailInvalido();
      return;
    }

    try {
      setSalvando(true);
      await adicionarCliente({
        nome: form.nome,
        telefone: form.telefone,
        email: form.email,
      });
      setModalCriar(false);
      setForm(clienteVazio);
    } catch (err) {
      console.error("Erro ao criar cliente:", err);
    } finally {
      setSalvando(false);
    }
  }

  async function handleEditar(event) {
    event.preventDefault();
    if (salvando) return;
    if (!modalEditar) return;

    if (!modalEditar.nome.trim()) {
      mostrarValidacao("common.fields.name");
      return;
    }
    if (!modalEditar.email.trim()) {
      mostrarValidacao("common.fields.email");
      return;
    }
    if (!modalEditar.telefone.trim()) {
      mostrarValidacao("common.fields.phone");
      return;
    }
    if (!emailRegex.test(modalEditar.email)) {
      mostrarEmailInvalido();
      return;
    }

    try {
      setSalvando(true);
      await editarCliente(modalEditar.id, {
        nome: modalEditar.nome,
        telefone: modalEditar.telefone,
        email: modalEditar.email,
      });
      setModalEditar(null);
    } catch (err) {
      console.error("Erro ao editar cliente:", err);
    } finally {
      setSalvando(false);
    }
  }

  return (
    <section className="section-clientes">
      <div className="conteudo-95 layout">
        <div className="conteudo">
          <h1 className="titulo t1">{t("clients.title")}</h1>

          <div className="filtros">
            <div className="input-container">
              <select
                className="input"
                value={ordem}
                onChange={(e) => setOrdem(e.target.value)}
              >
                <option value="">{t("common.order")}</option>
                <option value="az">{t("common.orderAZ")}</option>
                <option value="za">{t("common.orderZA")}</option>
              </select>
              <ChevronDown className="icon" size={18} />
            </div>
            <div className="input-container">
              <input
                type="text"
                placeholder={t("common.search")}
                className="input"
                value={busca}
                onChange={(e) => setBusca(e.target.value)}
              />
              <Search className="icon" size={18} />
            </div>
          </div>

          <div className="cards">
            {clientesFiltrados.length === 0 ? (
              <div className="clientes-vazio">{t("clients.empty")}</div>
            ) : (
              clientesFiltrados.map((c) => (
                <Card_linha
                  key={c.id}
                  titulo={c.nome}
                  informacoes={{
                    [t("common.fields.email")]: c.email,
                    [t("common.fields.phone")]: c.telefone,
                  }}
                  onEditar={() => setModalEditar({ ...c })}
                  onDeletar={() => removerCliente(c.id)}
                />
              ))
            )}
          </div>
        </div>
      </div>

      <button
        className="clientes-fab"
        title={t("clients.newButton")}
        onClick={() => {
          setForm(clienteVazio);
          setModalCriar(true);
        }}
      >
        <Plus size={24} />
      </button>

      {modalCriar && (
        <div className="modal">
          <form onSubmit={handleCriar}>
            <h2 className="modal-titulo">{t("clients.newTitle")}</h2>
            <FormCliente dados={form} onChange={setForm} />
            <div className="modal-acoes">
              <button
                type="button"
                className="btn-secundario"
                onClick={() => {
                  setModalCriar(false);
                  setForm(clienteVazio);
                }}
              >
                {t("common.cancel")}
              </button>
              <button type="submit" className="btn-primario" disabled={salvando}>
                {salvando ? t("common.saving") : t("common.create")}
              </button>
            </div>
          </form>
        </div>
      )}

      {modalEditar && (
        <div className="modal">
          <form onSubmit={handleEditar}>
            <h2 className="modal-titulo">{t("clients.editTitle")}</h2>
            <FormCliente dados={modalEditar} onChange={setModalEditar} />
            <div className="modal-acoes">
              <button
                type="button"
                className="btn-secundario"
                onClick={() => setModalEditar(null)}
              >
                {t("common.cancel")}
              </button>
              <button type="submit" className="btn-primario" disabled={salvando}>
                {salvando ? t("common.saving") : t("common.save")}
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default Clientes;
