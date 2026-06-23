import "./index.css";
import { Check, Eye, X } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { ConviteStatus, listarMeusConvites, responderConvite } from "../../services/conviteService";

const filtros = [
  { label: "Todos", value: "" },
  { label: "Pendentes", value: ConviteStatus.PENDENTE },
  { label: "Aceitos", value: ConviteStatus.ACEITO },
  { label: "Recusados", value: ConviteStatus.RECUSADO },
  { label: "Expirados", value: ConviteStatus.EXPIRADO },
];

const obterMensagemErro = (err, fallback) => err.data?.message || err.data?.error || fallback;

function ConvitesAtor() {
  const navigate = useNavigate();
  const [convites, setConvites] = useState([]);
  const [filtro, setFiltro] = useState(ConviteStatus.PENDENTE);
  const [carregando, setCarregando] = useState(true);
  const [processandoId, setProcessandoId] = useState(null);

  const carregarConvites = async (status = filtro) => {
    try {
      setCarregando(true);
      const dados = await listarMeusConvites(status || undefined);
      setConvites(dados);
    } catch (err) {
      console.error("Erro ao carregar convites do ator:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível carregar seus convites",
        text: obterMensagemErro(err, "Tente novamente em instantes"),
      });
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarConvites(filtro);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtro]);

  const responder = async (convite, status) => {
    try {
      setProcessandoId(convite.id);
      await responderConvite(convite.id, { status });
      await carregarConvites(filtro);

      Swal.fire({
        icon: "success",
        title: status === ConviteStatus.ACEITO ? "Convite aceito" : "Convite recusado",
        timer: 1800,
        showConfirmButton: false,
      });
    } catch (err) {
      console.error("Erro ao responder convite:", err);
      Swal.fire({
        icon: "error",
        title: "Não foi possível responder o convite",
        text: obterMensagemErro(err, "Verifique o convite e tente novamente"),
      });
    } finally {
      setProcessandoId(null);
    }
  };

  return (
    <main className="convites-ator-page">
      <header className="convites-ator-header">
        <span>Área do ator</span>
        <h1>Convites</h1>
      </header>

      <section className="convites-ator-panel">
        <div className="convites-ator-toolbar">
          <div>
            <h2>Meus convites</h2>
            <p>{convites.length} convite(s) encontrado(s)</p>
          </div>
          <select value={filtro} onChange={(event) => setFiltro(event.target.value)}>
            {filtros.map((item) => (
              <option key={item.label} value={item.value}>
                {item.label}
              </option>
            ))}
          </select>
        </div>

        <div className="convites-ator-list">
          {carregando ? (
            <p className="convites-ator-empty">Carregando convites...</p>
          ) : convites.length > 0 ? (
            convites.map((convite) => (
              <article key={convite.id} className="convites-ator-item">
                <div>
                  <h3>{convite.eventoTitulo}</h3>
                  <p>{convite.personagemNome} · {convite.personagemItemCodigo}</p>
                  <span className={`convites-ator-status status-${convite.status.toLowerCase()}`}>
                    {convite.status}
                  </span>
                </div>
                <div className="convites-ator-actions">
                  <button type="button" title="Ver evento" onClick={() => navigate(`/eventos/${convite.eventoId}`)}>
                    <Eye size={16} />
                  </button>
                  {convite.status === ConviteStatus.PENDENTE && (
                    <>
                      <button
                        type="button"
                        title="Aceitar convite"
                        disabled={Boolean(processandoId)}
                        onClick={() => responder(convite, ConviteStatus.ACEITO)}
                      >
                        <Check size={16} />
                      </button>
                      <button
                        type="button"
                        title="Recusar convite"
                        disabled={Boolean(processandoId)}
                        onClick={() => responder(convite, ConviteStatus.RECUSADO)}
                      >
                        <X size={16} />
                      </button>
                    </>
                  )}
                </div>
              </article>
            ))
          ) : (
            <p className="convites-ator-empty">Nenhum convite encontrado para este filtro.</p>
          )}
        </div>
      </section>
    </main>
  );
}

export default ConvitesAtor;
