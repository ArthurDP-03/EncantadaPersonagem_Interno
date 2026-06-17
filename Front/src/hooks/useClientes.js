import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import i18n from "../i18n";
import {
  getClientes,
  criarCliente,
  atualizarCliente,
  deletarCliente
} from "../services/cliente";

const t = i18n.t.bind(i18n);

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
      title: t('common.validation.invalidData'),
      html: formatarCamposValidacao(err.data.fields),
    });
    return;
  }

  mostrarErroGenerico(err, title, fallback);
};

export function useClientes() {
  const [clientes, setClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getClientes()
      .then(setClientes)
      .catch(err => {
        console.error("Erro completo:", err);

        const mensagemErro = obterMensagemErro(err, t('clients.errors.load'));
        setErro(mensagemErro);

        Swal.fire({
          icon: "error",
          title: t('clients.titles.load'),
          text: mensagemErro
        });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: t('common.confirm.deleteTitle', { item: t('common.items.client').toLowerCase() }),
      text: t('common.confirm.deleteText'),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: t('common.confirm.confirmBtn'),
      cancelButtonText: t('common.confirm.cancelBtn'),
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarCliente(id);

      setClientes(prev => prev.filter(c => c.id !== id));

      Swal.fire({
        icon: "success",
        title: t('common.success.deleted', { item: t('common.items.client') }),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar cliente:", err);

      mostrarErroGenerico(err, t('clients.titles.delete'), t('clients.errors.delete'));
    }
  };

  const criar = async (dados) => {
    try {
      const novoCliente = await criarCliente(dados);

      setClientes(prev => [...prev, novoCliente]);

      Swal.fire({
        icon: "success",
        title: t('common.success.created', { item: t('common.items.client') }),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar cliente:", err);

      mostrarErroValidacaoOuGenerico(err, t('clients.titles.create'), t('clients.errors.create'));

      throw err;
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarCliente(id, dados);

      setClientes(prev =>
        prev.map(c => (c.id === id ? atualizado : c))
      );

      Swal.fire({
        icon: "success",
        title: t('common.success.updated', { item: t('common.items.client') }),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao editar cliente:", err);

      mostrarErroValidacaoOuGenerico(err, t('clients.titles.update'), t('clients.errors.update'));

      throw err;
    }
  };

  return {
    clientes,
    carregando,
    erro,
    deletar,
    criar,
    editar
  };
}
