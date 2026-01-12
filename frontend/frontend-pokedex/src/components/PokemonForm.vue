<template>
  <div class="pokemon-form">
    <h2>{{ editingPokemon ? 'Editar Pokémon' : 'Novo Pokémon' }}</h2>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>Código *</label>
        <input v-model="form.codigo" type="text" required placeholder="Ex: 001" />
      </div>

      <div class="form-group">
        <label>Nome *</label>
        <input v-model="form.nome" type="text" required placeholder="Ex: Bulbasaur" />
      </div>

      <div class="form-group">
        <label>Tipo Primário *</label>
        <select v-model="form.tipoPrimario" required>
          <option value="">Selecione um tipo</option>
          <option v-for="tipo in tipos" :key="tipo.codigo" :value="tipo.codigo">
            {{ tipo.nome }}
          </option>
        </select>
      </div>

      <div class="form-group">
        <label>Tipo Secundário (Opcional)</label>
        <select v-model="form.tipoSecundario">
          <option value="">Nenhum</option>
          <option v-for="type in tipos" :key="type.codigo" :value="type.codigo">
            {{ type.nome }}
          </option>
        </select>
      </div>

      <div class="form-actions">
        <button type="button" @click="$emit('cancel')">Cancelar</button>
        <button type="submit" :disabled="loading">
          {{ loading ? 'Salvando...' : editingPokemon ? 'Atualizar' : 'Cadastrar' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import tipoService from '../services/tipoService.js';

const props = defineProps({
  pokemon: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['save', 'cancel']);

const tipos = ref([]);
const form = ref({
  codigo: '',
  nome: '',
  tipoPrimario: '',
  tipoSecundario: ''
});

const loadTipos = async () => {
  try {
    const response = await tipoService.getAll();
    tipos.value = response.data;
  } catch (error) {
    console.error('Erro ao carregar tipos:', error);
  }
};

const editingPokemon = computed(() => {
  return props.pokemon !== null && props.pokemon !== undefined});

watch(() => props.pokemon, (newPokemon) => {
  if (newPokemon) {
    form.value = {
      code: newPokemon.codigo,
      name: newPokemon.nome,
      tipoPrimario: newPokemon.tipoPrimario?.id || '',
      tipoSecundario: newPokemon.tipoSecundario?.id || ''
    };
  } else {
    resetForm();
  }
}, { immediate: true });

const resetForm = () => {
  form.value = {
    code: '',
    name: '',
    tipoPrimario: '',
    tipoSecundario: ''
  };
};

const handleSubmit = () => {
  const dataToSend = { ...form.value };
  if (!dataToSend.tipoSecundario) {
    delete dataToSend.tipoSecundario;
  }

  emit('save', dataToSend);
};

onMounted(() => {
  loadTipos();
});
</script>

<style scoped>
.pokemon-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  min-width: 400px;
  max-width: 500px;
}

h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input,
select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

input:focus,
select:focus {
  outline: none;
  border-color: #e3350d;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

button[type='button'] {
  background: #f0f0f0;
  color: #333;
}

button[type='submit'] {
  background: #e3350d;
  color: white;
}

button[type='submit']:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
