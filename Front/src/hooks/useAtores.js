import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import { getAtores, deletarAtor, criarAtor, atualizarAtor } from "../services/atoresService";

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

export function useAtores() {
  const [atores, setAtores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAtores()
      .then(setAtores)
      .catch(err => {
        console.error("Erro completo:", err);
        const mensagemErro = obterMensagemErro(err, "Erro ao carregar atores");
        setErro(mensagemErro);
        Swal.fire({ icon: "error", title: "Erro ao carregar", text: mensagemErro });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({ title: "Deseja deletar este ator?", text: "Esta ação não poderá ser desfeita.", icon: "warning", showCancelButton: true, confirmButtonText: "Deletar", cancelButtonText: "Cancelar", confirmButtonColor: "#d33" });
    if (!resultado.isConfirmed) return;
    try {
      await deletarAtor(id);
      setAtores(prev => prev.filter(a => a.id !== id));
      Swal.fire({ icon: "success", title: "Ator deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar ator:", err);
      mostrarErroGenerico(err, "Erro ao deletar", "Não foi possível deletar o ator");
    }
  };

  const criar = async (dados) => {
    try {
      const novoAtor = await criarAtor(dados);
      setAtores(prev => [...prev, novoAtor]);
      Swal.fire({ icon: "success", title: "Ator criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar ator:", err);
      mostrarErroValidacaoOuGenerico(err, "Erro ao criar", "Não foi possível criar o ator");
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarAtor(id, dados);
      setAtores(prev => prev.map(a => a.id === id ? atualizado : a));
      Swal.fire({ icon: "success", title: "Ator atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar ator:", err);
      mostrarErroValidacaoOuGenerico(err, "Erro ao atualizar", "Não foi possível atualizar o ator");
    }
  };

  return { atores, carregando, erro, deletar, criar, editar };
}
