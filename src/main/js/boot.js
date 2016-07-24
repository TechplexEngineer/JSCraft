//jscraft
var global = {};

global.plugin = null;

function __boot ( plugin, engine, classLoader ) {
	var logger = plugin.getLogger();
	logger.info('Booting!')
	global.plugin = plugin;
}