import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import {
  getEventos,
  criarEvento,
  atualizarEvento,
  deletarEvento,
} from "../services/eventosService";

const escapeHtml = (value) =>
  String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");

const formatarCamposValidacao = (fields) => {
  const itens = Object.entries(fields)
    .map(([campo, msg]) => `<li><strong>${escapeHtml(campo)}</strong>: ${escapeHtml(msg)}</li>`)
    .join("");

  return `<div style="text-align:left;"><ul style="margin:0;padding-left:1.25rem;">${itens}</ul></div>`;
};

const obterMensagemErro = (err, fallback) =>
  err.data?.message || err.data?.error || fallback;

const mostrarErroGenerico = (err, title, fallback) => {
  Swal.fire({
    icon: "error",
    title,
    text: obterMensagemErro(err, fallback),
  });
};

const mostrarErroValidacaoOuGenerico = (err, title, fallback) => {
  if (err.status === 422 && err.data?.fields) {
    Swal.fire({
      icon: "error",
      title: "Dados inválidos",
      html: formatarCamposValidacao(err.data.fields),
    });
    return;
  }

  mostrarErroGenerico(err, title, fallback);
};

export function useEventos() {
  const [eventos, setEventos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getEventos()
      .then(setEventos)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, "Erro ao carregar eventos");
        setErro(mensagemErro);
        Swal.fire({
          icon: "error",
          title: "Erro ao carregar",
          text: mensagemErro,
        });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: "Deseja deletar este evento?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#d33",
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarEvento(id);
      setEventos(prev => prev.filter(e => e.id !== id));
      Swal.fire({ icon: "success", title: "Evento deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar evento:", err);
      mostrarErroGenerico(err, "Erro ao deletar", "Não foi possível deletar o evento");
    }
  };

  const criar = async (dados) => {
    try {
      const novoEvento = await criarEvento(dados);
      setEventos(prev => [...prev, novoEvento]);
      Swal.fire({ icon: "success", title: "Evento criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar evento:", err);
      mostrarErroValidacaoOuGenerico(err, "Erro ao criar", "Não foi possível criar o evento");

      throw err;
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarEvento(id, dados);
      setEventos(prev => prev.map(e => (e.id === id ? atualizado : e)));
      Swal.fire({ icon: "success", title: "Evento atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar evento:", err);
      mostrarErroValidacaoOuGenerico(err, "Erro ao atualizar", "Não foi possível atualizar o evento");

      throw err;
    }
  };

  return { eventos, carregando, erro, criar, editar, deletar };
}
