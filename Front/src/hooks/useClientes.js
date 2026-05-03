import { useState, useEffect } from "react";
import { getClientes, criarCliente, atualizarCliente, deletarCliente } from "../services/cliente";

export function useClientes() {
  const [clientes, setClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarClientes = async () => {
    try {
      setCarregando(true);
      const data = await getClientes();
      setClientes(data);
      setErro(null);
    } catch (err) {
      console.error("Erro ao carregar clientes:", err);
      setErro(err.message || "Erro ao buscar clientes");
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarClientes();
  }, []);

  const adicionarCliente = async (cliente) => {
    try {
      await criarCliente(cliente);
      await carregarClientes();
    } catch (err) {
      console.error("Erro ao criar cliente:", err);
      throw err;
    }
  };

  const editarCliente = async (id, cliente) => {
    try {
      await atualizarCliente(id, cliente);
      await carregarClientes();
    } catch (err) {
      console.error("Erro ao atualizar cliente:", err);
      throw err;
    }
  };

  const removerCliente = async (id) => {
    try {
      await deletarCliente(id);
      await carregarClientes();
    } catch (err) {
      console.error("Erro ao deletar cliente:", err);
      throw err;
    }
  };

  return { clientes, carregando, erro, adicionarCliente, editarCliente, removerCliente };
}
